# Android Module - ChemDom

Módulo Android do jogo ChemDom usando libGDX.

## Estrutura

```
android/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── domino/
│       │           └── android/
│       │               └── AndroidLauncher.java
│       ├── res/
│       │   ├── values/
│       │   │   ├── strings.xml
│       │   │   └── colors.xml
│       │   └── drawable-nodpi/
│       │       └── app_icon.xml
│       └── AndroidManifest.xml
└── build.gradle
```

## Requisitos

- Android SDK 34 (Configurado em `android/build.gradle`)
- Suporte mínimo: API 24 (Android 7.0)
- Java 21 ou superior

## Compilação

Para compilar o módulo Android:

```bash
./gradlew android:build
```

Para gerar o APK de release:

```bash
./gradlew android:assembleRelease
```

Para gerar o APK de debug:

```bash
./gradlew android:assembleDebug
```

## Instalação

Para instalar o APK em um dispositivo/emulador conectado:

```bash
./gradlew android:installDebug
```

## Configuração

### Permissões

As permissões podem ser adicionadas no `AndroidManifest.xml`. Atualmente inclui:
- `VIBRATE` - Para feedback de vibração

### Recursos

Adicione ícones, strings e outras resources em:
- `res/drawable-*` - Imagens e drawables
- `res/values/strings.xml` - Strings
- `res/values/colors.xml` - Cores

## Notas

- O módulo usa a mesma classe `Main` do core
- Os assets são compartilhados com todas as plataformas (pasta `assets/` no root)
- O ícone padrão é um SVG placeholder - considere substituir por um ícone real
