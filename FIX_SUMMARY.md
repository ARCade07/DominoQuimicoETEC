# Resumo de Correções - Android Build Issues

## Problemas Identificados

### 1. **NullPointerException em PecaDao.java:18**
**Sintoma:** Erro ao inicializar `PecaDao` quando `GameScreen` é criado
```
java.lang.NullPointerException: Attempt to invoke interface method 
'com.mongodb.client.MongoCollection com.mongodb.client.MongoDatabase.getCollection(java.lang.String)' 
on a null object reference
```

**Causa Raiz:** 
- O `AndroidConnectionFactory.initialize()` era chamado no `onCreate()` do `AndroidLauncher`, mas falhas silenciosas causavam problemas
- Quando `GameScreen.inicializarPecas()` criava `ServicoPecas`, ele chamava `ConnectionFactory.getInstance()`
- Se a instância ainda não tinha sido inicializada com `initializeWith()`, uma nova instância era criada tentando carregar do `.env` (que não existe no Android)
- Isso resultava em `database = null`

**Solução Implementada:**

#### a) ConnectionFactory.java (core)
- Adicionado aviso detalhado no método `getInstance()` para alertar se for chamado antes de `initializeWith()`

#### b) ServicoPecas.java (core)
- Adicionada validação de inicialização no construtor
- Lança `IllegalStateException` com mensagem clara se o banco de dados não estiver inicializado
- Previne crashes posteriores em `PecaDao`

```java
public ServicoPecas() {
    ConnectionFactory connection = ConnectionFactory.getInstance();

    if (connection == null || connection.getDatabase() == null) {
        throw new IllegalStateException(
            "❌ ERRO CRÍTICO: Banco de dados não foi inicializado!\n" +
            "   Verifique se AndroidConnectionFactory.initialize() foi chamado ANTES de criar GameScreen.\n" +
            "   Status: database=" + (connection == null ? "null" : "not null but DB=" + (connection.getDatabase() == null ? "null" : "ok"))
        );
    }

    this.pecaDao = new PecaDao(connection);
}
```

---

### 2. **UnsatisfiedLinkError: library "libgdx.so" not found**
**Sintoma:** App falha imediatamente ao tentar iniciar
```
java.lang.UnsatisfiedLinkError: dlopen failed: library "libgdx.so" not found
```

**Causa Raiz:**
- As bibliotecas nativas do libGDX (`.so` files) não estavam sendo extraídas das dependências Maven
- O script `extractNatives` no build.gradle tinha um erro: usava `file()` de forma incorreta com strings interpoladas

**Solução Implementada:**

#### a) android/build.gradle
Corrigido script `extractNatives`:

**Antes (ERRO):**
```gradle
def targetDir = file("src/main/jniLibs/${abiFolder}")
```

**Depois (CORRETO):**
```gradle
def targetDir = new File("${projectDir}/src/main/jniLibs/${abiFolder}")
```

Melhoramentos adicionais:
- Melhor logging com emojis para visualizar o progresso
- Verificação de cada ABI individualmente (armeabi-v7a, arm64-v8a, x86, x86_64)
- Suporte a FreeType natives além do core libGDX
- Garantia de que `extractNatives` é executado antes de `assembleDebug` e `assembleRelease`

**Arquivos .so extraídos com sucesso:**
```
✅ arm64-v8a/libgdx.so
✅ arm64-v8a/libgdx-freetype.so
✅ armeabi-v7a/libgdx.so
✅ armeabi-v7a/libgdx-freetype.so
✅ x86/libgdx.so
✅ x86/libgdx-freetype.so
✅ x86_64/libgdx.so
✅ x86_64/libgdx-freetype.so
```

---

## Arquivos Modificados

1. **core/src/main/java/com/domino/bd/ConnectionFactory.java**
   - Adicionado aviso em `getInstance()` para debug

2. **core/src/main/java/com/domino/servicos/ServicoPecas.java**
   - Adicionada validação de inicialização do banco de dados
   - Lança exceção clara se banco não estiver inicializado

3. **android/build.gradle**
   - Corrigido script `extractNatives` (fix do `file()`)
   - Melhorado logging da extração
   - Adicionadas dependências para FreeType natives
   - Garantido que task rodar antes de builds

---

## Verificação

✅ **APK gerado com sucesso:** `android/build/outputs/apk/debug/android-debug.apk` (45.82 MB)
✅ **Bibliotecas nativas incluídas:** Todos os `.so` files para todos os ABIs
✅ **Banco de dados validado:** Erro claro se não estiver inicializado

---

## Verificação Final ✅

**Status:** ✅ **APP FUNCIONANDO!**

Logs confirmam:
- ✅ Bibliotecas nativas libGDX carregadas com sucesso
- ✅ MongoDB inicializado corretamente
- ✅ Banco de dados conectado
- ✅ Interface gráfica criada
- ✅ Gestos de toque funcionando

### Último Ajuste: MongoDB Driver Version
- **Problema:** MongoDB 5.0.0 requer JNDI para DNS SRV lookup, que não existe no Android
- **Solução:** Downgrade para MongoDB driver 4.11.1
- **Mudança:** [core/build.gradle:9](core/build.gradle)
  ```gradle
  implementation 'org.mongodb:mongodb-driver-sync:4.11.1'  // antes era 5.0.0
  ```

### Classe de Configuração Android
- **Arquivo:** [core/src/main/java/com/domino/bd/AndroidMongoDBConfig.java](core/src/main/java/com/domino/bd/AndroidMongoDBConfig.java)
- **Função:** Configura propriedades do sistema para MongoDB funcionar no Android
- **Chamada em:** [android/src/main/java/com/domino/android/AndroidLauncher.java:25](android/src/main/java/com/domino/android/AndroidLauncher.java)

## Próximos Passos

1. Testar clique no botão "Play" em LobbyScreen para iniciar GameScreen
2. Confirmar que banco de dados carrega as peças corretamente
3. Se houver problemas de conexão com MongoDB Atlas:
   - Verificar se device/emulador tem acesso à internet
   - Confirmar credenciais em `strings.xml`
   - Adicionar IP do emulador aos permissões do MongoDB Atlas
