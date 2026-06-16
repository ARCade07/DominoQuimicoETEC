# Análise de Compatibilidade Android - ChemDom

**Data**: 15/06/2026  
**Versão**: 1.0  
**Status**: ⚠️ CRÍTICO - Problemas encontrados

---

## 📋 Sumário Executivo

O aplicativo **NÃO funcionará corretamente** quando instalado em um dispositivo Android. Foram identificados **4 problemas críticos** e **3 problemas graves** que impedem a execução normal do app.

### Problemas Críticos (Impedem funcionamento):
1. ❌ Arquivo `.env` não está disponível em Android
2. ❌ Permissões de rede não configuradas
3. ❌ Dependência `jakarta.mail` pode causar crash em Android
4. ❌ Sem tratamento de erros de conexão ao BD

---

## 🔴 Problema 1: Arquivo .env Faltando

### Descrição
O aplicativo carrega variáveis de ambiente do arquivo `.env` na linha 18 de `ConnectionFactory.java`:

```java
Dotenv dotenv = Dotenv.load();
String connectionString = dotenv.get("MONGO_URI");
String databaseName = dotenv.get("DATABASE_NAME");
```

### Por que é um problema
- O arquivo `.env` está no `.gitignore` (linha 157) por **segurança**
- **O arquivo NÃO será incluído no APK** quando o app é construído
- Quando o app tenta conectar ao BD na tela de login, receberá `null` ao tentar ler as variáveis
- Isso causará uma exceção `NullPointerException` e o app fará **crash**

### Impacto
- ❌ **App crasha na tela de login**
- ❌ Impossível fazer login
- ❌ Impossível registrar novo usuário
- ❌ Impossível recuperar senha

### Fluxo de erro:
```
Usuário abre app
    ↓
Main.create() → LoginScreen()
    ↓
LoginScreen constructor → ConnectionFactory.getInstance()
    ↓
ConnectionFactory carrega .env
    ↓
Dotenv.load() retorna NULL (arquivo não existe no APK)
    ↓
dotenv.get("MONGO_URI") → NullPointerException
    ↓
🔴 APP CRASHA
```

---

## 🔴 Problema 2: Permissões de Rede Não Configuradas

### Descrição
O `AndroidManifest.xml` está faltando permissões essenciais:

```xml
<!-- FALTA NO MANIFEST: -->
<uses-permission android:name="android.permission.INTERNET" />
```

### Por que é um problema
- MongoDB está em um servidor remoto (via `MONGO_URI`)
- Android **exige permissão explícita** para acessar a rede (desde API 21)
- Sem essa permissão, a conexão ao MongoDB será bloqueada pelo SO
- A app tentará conectar, mas receberá `SecurityException`

### Impacto
- ❌ **Conexão ao BD é bloqueada**
- ❌ Todas as operações de rede falham
- ❌ App fica travado esperando resposta da rede

---

## 🔴 Problema 3: Dependência jakarta.mail em Android

### Descrição
O `core/build.gradle` inclui:

```gradle
implementation 'com.sun.mail:jakarta.mail:2.0.2'
```

### Por que é um problema
- Esta biblioteca é usada para envio de emails (recuperação de senha)
- Depende de APIs Java que **não estão disponíveis em Android**
- Especificamente, precisa de classes como `javax.activation.DataSource` que não existem no Android Runtime
- Isso pode causar `ClassNotFoundException` em tempo de execução

### Impacto
- ⚠️ **Funcionalidade de "Esqueceu a senha" pode falhar**
- ⚠️ Possível crash ao tentar usar recuperação de senha

---

## 🟠 Problema 4: Sem Tratamento de Erros de Conexão

### Descrição
Quando `ConnectionFactory` tenta conectar ao BD e falha:

```java
try {
    mongoClient = MongoClients.create(settings);
    database = mongoClient.getDatabase(dotenv.get("DATABASE_NAME"));
    System.out.println("Conexão estabelecida com sucesso ao MongoDB!");
} catch (Exception e) {
    System.err.println("Erro ao conectar ao MongoDB: " + e.getMessage());
    e.printStackTrace();
    // ❌ NÃO FAZ NADA AQUI!
    // database fica NULL
}
```

### Por que é um problema
1. A variável `database` fica `null` se houver erro
2. Quando `UsuarioDao` tenta usar:
   ```java
   this.docsUsuarios = connection.getDatabase().getCollection("usuarios");
   ```
   Receberá `NullPointerException`

3. O app não mostra mensagem clara ao usuário

### Impacto
- ❌ Crash silencioso sem avisar o usuário
- ⚠️ Difícil de debugar em dispositivo real
- ⚠️ Pior experiência de usuário possível

---

## 🟠 Problema 5: Timeout de Rede Não Configurado

### Descrição
MongoDB driver não tem timeout configurado em `MongoClientSettings`

### Por que é um problema
- Se o servidor estiver offline, a app **fica travada indefinidamente**
- Usuário não consegue fechar a app normalmente
- Drena bateria

### Impacto
- ⚠️ App pode travar por tempo indeterminado
- ⚠️ Pior experiência em redes lentas

---

## 🟠 Problema 6: Sem Cache Local para Offline

### Descrição
Não há banco de dados local (SQLite/Room) como fallback

### Por que é um problema
- Se a rede estiver indisponível, o app é completamente inutilizável
- Usuário não consegue nem fazer login

### Impacto
- ⚠️ Sem modo offline
- ⚠️ Depende 100% da conectividade de rede

---

## 🟠 Problema 7: Classpath Scanning não funciona em Android

### Descrição
Se houver código que depende de reflection/classpath scanning (comum em frameworks)

### Por que é um problema
- Android obfusca e otimiza classes em tempo de compilação (D8/R8)
- Código que escaneia classpath em runtime pode não funcionar

---

## ✅ Soluções Recomendadas

### Solução 1: Configurar variáveis de ambiente corretamente (CRÍTICO)

**Opção A: Variáveis em Strings.xml (Recomendado)**

```xml
<!-- android/src/main/res/values/strings.xml -->
<resources>
  <string name="app_name">ChemDom</string>
  <string name="mongo_uri">YOUR_MONGO_URI_HERE</string>
  <string name="database_name">YOUR_DATABASE_NAME</string>
</resources>
```

**Opção B: BuildConfig (Para diferentes ambientes)**

```gradle
// android/build.gradle
android {
  defaultConfig {
    buildConfigField "String", "MONGO_URI", "\"${System.getenv('MONGO_URI') ?: 'mongodb://localhost'}\""
    buildConfigField "String", "DATABASE_NAME", "\"${System.getenv('DATABASE_NAME') ?: 'default'}\""
  }
}
```

### Solução 2: Adicionar permissão de INTERNET (CRÍTICO)

```xml
<!-- android/src/main/AndroidManifest.xml -->
<uses-permission android:name="android.permission.INTERNET" />
```

### Solução 3: Criar ConnectionFactory específico para Android

```java
// android/src/main/java/com/domino/android/AndroidConnectionFactory.java
public class AndroidConnectionFactory {
  public static void initialize(Context context) {
    String mongoUri = context.getString(R.string.mongo_uri);
    String dbName = context.getString(R.string.database_name);
    
    // Usar esses valores em ConnectionFactory
    ConnectionFactory.initializeWith(mongoUri, dbName);
  }
}
```

E no `AndroidLauncher.onCreate()`:
```java
@Override
protected void onCreate(Bundle savedInstanceState) {
  super.onCreate(savedInstanceState);
  AndroidConnectionFactory.initialize(this);
  // ... resto do código
}
```

### Solução 4: Adicionar timeout de rede

```java
// core/src/main/java/com/domino/bd/ConnectionFactory.java
MongoClientSettings settings = MongoClientSettings.builder()
    .applyConnectionString(connString)
    .applyToConnectionPoolSettings(builder ->
        builder.maxConnectionIdleTime(30, TimeUnit.SECONDS)
    )
    .applyToSocketSettings(builder ->
        builder.connectTimeout(10, TimeUnit.SECONDS)
               .readTimeout(10, TimeUnit.SECONDS)
    )
    .build();
```

### Solução 5: Melhorar tratamento de erros

```java
// Modificar ConnectionFactory
public class ConnectionFactory {
  private static boolean isConnected = false;
  
  private ConnectionFactory() {
    try {
      // ... código de conexão
      isConnected = true;
    } catch (Exception e) {
      isConnected = false;
      System.err.println("Erro ao conectar ao MongoDB: " + e.getMessage());
      // Registrar erro para debug
      saveErrorLog(e);
    }
  }
  
  public static boolean isConnected() {
    return isConnected;
  }
}
```

E verificar antes de usar:
```java
// LoginScreen
if (!ConnectionFactory.isConnected()) {
  popUp.showErro("Sem conexão com o servidor. Tente novamente mais tarde.");
  return;
}
```

### Solução 6: Exclude jakarta.mail em Android (se possível)

```gradle
// android/build.gradle
dependencies {
  implementation project(':core') {
    exclude group: 'com.sun.mail', module: 'jakarta.mail'
  }
}
```

Ou criar um módulo alternativo para email em Android.

---

## 📊 Resumo de Correções Necessárias

| Problema | Severidade | Solução | Prioridade |
|----------|-----------|---------|-----------|
| .env faltando | 🔴 Crítico | Opção A ou B acima | 1 |
| Sem permissão INTERNET | 🔴 Crítico | Adicionar ao Manifest | 1 |
| jakarta.mail | 🔴 Crítico | Excluir/Substituir | 1 |
| Sem tratamento de erro | 🟠 Grave | Adicionar verificações | 2 |
| Sem timeout | 🟠 Grave | Configurar em Settings | 2 |
| Sem offline | 🟠 Grave | Implementar SQLite cache | 3 |

---

## 🧪 Checklist de Testes Pré-Deploy

Antes de lançar a app, testar:

- [ ] Fazer login com credenciais corretas
- [ ] Tentar login com credenciais erradas
- [ ] Testar "Esqueceu a senha" - envio de email
- [ ] Testar em rede 4G
- [ ] Testar em Wi-Fi
- [ ] Simular conexão lenta (usar DevTools)
- [ ] Simular conexão offline
- [ ] Testar recuperação quando conexão volta
- [ ] Verificar logs de erro em dispositivo real
- [ ] Testar em Android 7.0+ (API 24)
- [ ] Testar em Android 14+ (API 34)

---

## 📌 Conclusão

**O app FALHARÁ ao ser instalado em um dispositivo Android real** devido aos problemas mencionados acima, especialmente o arquivo `.env` faltando.

As correções são relativamente simples, mas **CRÍTICAS** para funcionar em Android.

**Tempo estimado de correção: 30-45 minutos**

