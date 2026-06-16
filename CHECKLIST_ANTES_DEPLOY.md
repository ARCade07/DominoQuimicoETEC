# ✅ Checklist Pré-Deployment Android - ChemDom

Siga este checklist antes de fazer deploy do app em produção.

---

## 🔴 CRÍTICO - Deve ser feito antes de qualquer teste

### Configuração do MongoDB

```bash
# PASSO 1: Editar android/src/main/res/values/strings.xml
```

Procure por:
```xml
<string name="mongo_uri">mongodb://localhost:27017</string>
<string name="database_name">domino_db</string>
```

E altere para **seus valores reais**:
```xml
<!-- Exemplo para MongoDB Atlas -->
<string name="mongo_uri">mongodb+srv://usuario:senha@cluster.mongodb.net</string>
<string name="database_name">seu_banco_de_dados_aqui</string>
```

**Salvou?** Proceda para próxima seção ✓

---

## 🟡 IMPORTANTE - Verificações Antes de Compilar

### ✅ Permissões de Rede
```bash
# Verificar se o arquivo tem as permissões corretas:
grep -A 2 "INTERNET" android/src/main/AndroidManifest.xml
```

Deve mostrar:
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

**Verificado?** ✓

### ✅ Arquivo .env (não deve ser necessário)
```bash
# Se estiver usando .env para desenvolvimento, está OK
# Mas em Android, as configurações vêm de strings.xml
ls -la .env 2>/dev/null && echo "Arquivo .env encontrado (dev only)" || echo "Nenhum .env (correto para produção)"
```

**Verificado?** ✓

---

## 🟢 COMPILAÇÃO

### Opção 1: APK Debug (para testar)
```bash
./gradlew clean android:assembleDebug
```

Resultado esperado:
```
✓ BUILD SUCCESSFUL
APK: android/build/outputs/apk/debug/android-debug.apk (6-7 MB)
```

- [ ] Compilou sem erros
- [ ] APK foi gerado
- [ ] Transferir para dispositivo: `adb install android/build/outputs/apk/debug/android-debug.apk`

### Opção 2: APK Release (para Play Store)
```bash
./gradlew clean android:assembleRelease
```

Resultado esperado:
```
✓ BUILD SUCCESSFUL
APK: android/build/outputs/apk/release/android-release-unsigned.apk (5-6 MB)
```

- [ ] Compilou sem erros
- [ ] APK foi gerado
- [ ] **IMPORTANTE**: Ainda precisa ser assinado antes de enviar para Play Store

---

## 🧪 TESTES EM DISPOSITIVO

### Teste 1: Inicialização
- [ ] App abre sem crashes
- [ ] Tela de login é exibida
- [ ] Verificar logs: `adb logcat | grep "ChemDom"`

Deve ver:
```
✓ Conexão estabelecida com sucesso ao MongoDB!
```

### Teste 2: Login Válido
- [ ] Entrar com email/senha corretos
- [ ] Deve navegar para StartScreen
- [ ] Verificar logs de sucesso

### Teste 3: Login Inválido
- [ ] Tentar com email/senha errados
- [ ] Deve mostrar pop-up de erro
- [ ] Não deve crashear

### Teste 4: Sem Conexão
```bash
# No Android, desabilitar internet via Settings → Developer options
# Ou usar: adb shell svc wifi disable
```

- [ ] Clicar em "Entrar"
- [ ] Deve mostrar: "Erro: Sem conexão com o servidor"
- [ ] Não deve travar

### Teste 5: Reconexão
```bash
# Reabilitar internet
adb shell svc wifi enable
```

- [ ] Tentar login novamente
- [ ] Deve funcionar normalmente

### Teste 6: Diferentes Versões Android
- [ ] Testar em Android 7.0 (API 24)
- [ ] Testar em Android 10.0 (API 29)
- [ ] Testar em Android 14.0 (API 34)

---

## 📊 MONITORAMENTO

### Logs Importantes
```bash
# Ver todos os logs do app
adb logcat | grep "ChemDom"

# Ver apenas erros
adb logcat | grep -E "Error|Exception|❌"

# Ver status de conexão
adb logcat | grep "Conexão\|Connection"

# Salvar logs em arquivo
adb logcat > logcat.txt
```

### Crash Report
Se o app crashear, ver:
```bash
adb logcat | tail -50
```

Procurar por:
- Exception stacktrace
- Linha que causou o erro
- Mensagem de erro específica

---

## 🚀 DEPLOY PARA PLAY STORE

### 1. Preparar APK com Assinatura

```bash
# Criar keystore (primeira vez apenas)
keytool -genkey -v -keystore release.keystore \
  -keyalg RSA -keysize 2048 -validity 10000 -alias prodkey

# Assinar APK
jarsigner -verbose -sigalg SHA1withRSA -digestalg SHA1 \
  -keystore release.keystore \
  android/build/outputs/apk/release/android-release-unsigned.apk prodkey

# Alinhar APK
zipalign -v 4 android-release-unsigned.apk android-release-signed.apk

# Resultado
ls -lh android-release-signed.apk
```

- [ ] APK assinado criado (~5-6 MB)

### 2. Verificar Manifest
```bash
aapt dump badging android-release-signed.apk | head -10
```

Deve mostrar:
```
package: name='com.domino.android'
launchable-activity: name='com.domino.android.AndroidLauncher'
```

- [ ] Package name correto
- [ ] Activity principal identificada

### 3. Upload para Play Store
```
1. Acessar: https://play.google.com/console
2. Criar novo app ou abrir existente
3. Upload: APK assinado
4. Preencher informações:
   - Título
   - Descrição
   - Screenshots
   - Categorias
5. Revisar e publicar
```

- [ ] Aplicativo criado/atualizado
- [ ] APK enviado
- [ ] Todas as informações preenchidas
- [ ] Pronto para revisão do Google

---

## 🔍 VERIFICAÇÃO FINAL

### Antes de considerar "pronto":

- [ ] MongoDB URI configurado em strings.xml
- [ ] Permissões de rede no Manifest
- [ ] APK compilado com sucesso
- [ ] Testes básicos passando
- [ ] Login funciona
- [ ] Sem internet mostra erro apropriado
- [ ] Logs aparecem corretamente
- [ ] Sem crashes observados
- [ ] APK assinado (para Play Store)
- [ ] Informações do app preenchidas

---

## ⚠️ PROBLEMAS COMUNS

### "Erro: Sem conexão com o servidor"

**Causas:**
1. MongoDB URI errada em strings.xml
2. Servidor MongoDB offline
3. Dispositivo sem internet
4. Firewall bloqueando

**Solução:**
```bash
# Verificar strings.xml
cat android/src/main/res/values/strings.xml | grep mongo_uri

# Verificar internet
ping google.com

# Verificar logs
adb logcat | grep "MongoDB"
```

### "E-mail ou senha incorretos" mesmo com dados válidos

**Causas:**
1. Usuário não registrado
2. Senha alterada
3. Problema de conexão (vê erro anterior)

**Solução:**
1. Tentar registrar novo usuário via "Cadastrar-se"
2. Ou verificar no MongoDB:
```javascript
db.usuarios.findOne({email: "seu@email.com"})
```

### App crasha ao abrir

**Solução:**
```bash
# Ver stack trace
adb logcat -v threadtime | tail -100

# Procurar por "Exception" ou "Error"
adb logcat | grep -E "Exception|Error"

# Se for problema de strings.xml
# Verificar se os valores estão corretos
cat android/src/main/res/values/strings.xml
```

---

## 📞 SUPORTE

Se enfrentar problemas não listados:

1. Ver `ANALISE_COMPATIBILIDADE_ANDROID.md`
2. Ver `CONFIGURACAO_ANDROID.md`
3. Verificar logs via `adb logcat`
4. Procurar por erro específico no Google
5. Considerar Firebase Crashlytics para monitoramento automático

---

## ✨ DEPOIS DE DEPLOY

### Monitoramento Recomendado

1. **Firebase Crashlytics**
   - Detecta crashes automaticamente
   - Agrupa por tipo de erro
   - Notificações em tempo real

2. **Firebase Analytics**
   - Rastreia eventos importantes
   - Padrões de uso
   - Funnels de conversão

3. **Server Monitoring**
   - MongoDB Atlas Dashboard
   - Latência de rede
   - Uso de dados

---

**Checklist completo?** ✅ Pronto para deploy!

