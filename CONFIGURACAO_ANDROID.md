# Configuração do ChemDom para Android

## 📋 Visão Geral

Este guia explica como configurar corretamente o ChemDom para funcionar em um dispositivo Android.

---

## 🔧 Pré-requisitos

- Android SDK 24+ instalado (Android 7.0+)
- Dispositivo Android ou emulador
- Acesso ao servidor MongoDB (URI e nome do banco)
- Conexão de internet no dispositivo

---

## ⚙️ Configuração Obrigatória

### 1️⃣ Configurar URL do MongoDB

Edite o arquivo `android/src/main/res/values/strings.xml`:

```xml
<resources>
  <string name="app_name">ChemDom</string>
  <!-- ALTERAR PARA SEU SERVIDOR REAL -->
  <string name="mongo_uri">mongodb+srv://user:password@cluster.mongodb.net</string>
  <string name="database_name">seu_banco_de_dados</string>
</resources>
```

**Opções de conexão MongoDB:**

- **Local**: `mongodb://localhost:27017`
- **MongoDB Atlas**: `mongodb+srv://user:password@cluster.mongodb.net`
- **Servidor remoto**: `mongodb://seu-servidor.com:27017`

### 2️⃣ Verificar Permissões (AndroidManifest.xml)

O arquivo já está configurado com as permissões necessárias:

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.VIBRATE" />
```

Não há necessidade de alterar isso para versões recentes do Android.

---

## 🚀 Compilação e Instalação

### Compilar APK Debug

```bash
./gradlew android:assembleDebug
```

APK gerado: `android/build/outputs/apk/debug/android-debug.apk`

### Compilar APK Release (sem assinatura)

```bash
./gradlew android:assembleRelease
```

APK gerado: `android/build/outputs/apk/release/android-release-unsigned.apk`

### Instalar em dispositivo/emulador

```bash
./gradlew android:installDebug
```

### Executar no emulador

```bash
./gradlew android:run
```

---

## 🧪 Testes de Verificação

Após instalar o app, testar:

### ✅ Teste 1: Verificar Conexão com BD
1. Abrir o app
2. Ver se a tela de login carrega
3. Verificar logs do Logcat:
   ```
   ✓ Conexão estabelecida com sucesso ao MongoDB!
   ```

### ✅ Teste 2: Login com Credenciais Válidas
1. Tentar fazer login com email e senha corretos
2. Deve navegar para a tela StartScreen (ou TeacherScreen se for professor)

### ✅ Teste 3: Login com Credenciais Inválidas
1. Tentar fazer login com email/senha errados
2. Deve mostrar mensagem de erro

### ✅ Teste 4: Sem Conexão de Rede
1. Desativar internet no dispositivo
2. Tentar fazer login
3. Deve mostrar mensagem: "Erro: Sem conexão com o servidor"

### ✅ Teste 5: Rede Lenta
1. Simular rede lenta via Android Studio DevTools
2. O app deve não travar (timeout de 10 segundos está configurado)

---

## 🔍 Debugging

### Ver Logs em Tempo Real

```bash
adb logcat | grep "ChemDom"
```

### Filtrar por erros

```bash
adb logcat | grep "Error\|Exception"
```

### Ver todas as linhas de BD

```bash
adb logcat | grep "MongoDB\|ConnectionFactory"
```

### Screenshots da tela

```bash
adb shell screencap -p /sdcard/screenshot.png
adb pull /sdcard/screenshot.png
```

---

## ⚠️ Troubleshooting

### Problema: "Sem conexão com o servidor"

**Causas possíveis:**
1. ❌ URL do MongoDB incorreta em `strings.xml`
2. ❌ Servidor MongoDB offline
3. ❌ Dispositivo sem internet
4. ❌ Firewall bloqueando a porta 27017

**Soluções:**
- [ ] Verificar URL em `strings.xml`
- [ ] Testar conexão: `ping seu-servidor.mongodb.net`
- [ ] Verificar internet: Settings → Network
- [ ] Verificar firewall/proxy

### Problema: "E-mail ou senha incorretos" mesmo com credenciais válidas

**Causas possíveis:**
1. ❌ Email não está registrado no BD
2. ❌ Senha foi alterada
3. ❌ Problema de encoding (caracteres especiais)

**Soluções:**
- [ ] Registrar novo usuário no app
- [ ] Testar com credenciais conhecidas
- [ ] Verificar no MongoDB se o usuário existe

### Problema: App crasha ao abrir

**Causas possíveis:**
1. ❌ Erro ao ler strings.xml
2. ❌ Exceção na inicialização do ConnectionFactory
3. ❌ Problema com dependências

**Soluções:**
```bash
# Ver stack trace completo
adb logcat -v threadtime | grep "ChemDom"

# Reinstalar app
./gradlew android:installDebug --reinstall

# Limpar cache
./gradlew clean android:assembleDebug
```

### Problema: APK muito grande

Tamanho esperado:
- **Debug APK**: 6-7 MB
- **Release APK**: 5-6 MB

Se muito maior, verificar bibliotecas desnecessárias.

---

## 🔐 Segurança em Produção

### Antes de lançar na Play Store:

1. **Não commitar credenciais** - Use variáveis de build
2. **Usar HTTPS/MongoDB Atlas** - Nunca usar mongo:// sem segurança
3. **Assinatura do APK** - Criar keystore
4. **Ofuscação** - Habilitar R8/Proguard
5. **Testes** - Testar em vários dispositivos

### Assinatura do APK para Release

```bash
# Criar keystore (uma única vez)
keytool -genkey -v -keystore release.keystore \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -alias prodkey

# Assinar APK
jarsigner -verbose -sigalg SHA1withRSA -digestalg SHA1 \
  -keystore release.keystore \
  android-release-unsigned.apk prodkey

# Alinhar APK
zipalign -v 4 android-release-unsigned.apk android-release.apk
```

---

## 📊 Monitoramento em Produção

### Ativar logs estruturados

Adicionar em `ConnectionFactory.java`:

```java
System.out.println("[CONNECTION] " + status);
// Vai aparecer em Logcat e pode ser coletado por ferramentas
```

### Monitorar servidores de BD

Use MongoDB Atlas Dashboard para:
- Ver conexões ativas
- Monitorar latência
- Alertas de performance

---

## 🐛 Relatório de Bugs

Se encontrar problemas, incluir:

1. Versão do Android (ex: 10, 11, 12)
2. Modelo do dispositivo
3. Stack trace do logcat
4. Passos para reproduzir
5. Comportamento esperado vs observado

---

## 📱 Compatibilidade Testada

| Android | API | Status |
|---------|-----|--------|
| 7.0     | 24  | ✅ Suportado |
| 8.0     | 26  | ✅ Suportado |
| 9.0     | 28  | ✅ Suportado |
| 10.0    | 29  | ✅ Suportado |
| 11.0    | 30  | ✅ Suportado |
| 12.0    | 31  | ✅ Suportado |
| 13.0    | 33  | ✅ Suportado |
| 14.0    | 34  | ✅ Suportado |

---

## 📚 Recursos Adicionais

- [Android Developer Docs](https://developer.android.com)
- [MongoDB Java Driver](https://www.mongodb.com/docs/drivers/java/sync/current/)
- [libGDX Wiki](https://libgdx.com/wiki/)
- [Android Permissions](https://developer.android.com/guide/topics/permissions/overview)

---

## ✨ Próximos Passos

Depois de configurado e testado:

1. [ ] Teste em dispositivo real (não apenas emulador)
2. [ ] Teste em diferentes versões do Android
3. [ ] Teste com diferentes redes (4G, 5G, Wi-Fi)
4. [ ] Teste modo offline
5. [ ] Teste com servidor MongoDB em produção
6. [ ] Implementar crash reporting (Firebase Crashlytics)
7. [ ] Adicionar analytics (Firebase Analytics)

