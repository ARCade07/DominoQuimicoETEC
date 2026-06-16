# Sistema de Gestos do Android

Documentação do sistema de detecção de gestos padrão do Android implementado no ChemDom.

## Visão Geral

O sistema de gestos está totalmente integrado na arquitetura do jogo, permitindo que qualquer tela (Screen) detecte e responda aos gestos mais comuns do Android:

1. **Back Gesture** - Arrastar no canto esquerdo para voltar
2. **Pinch Zoom** - Pinçar para zoom in/out
3. **Two-Finger Pan** - Arrastar com 2 dedos para mover câmera
4. **Single-Finger Drag** - Arrastar com 1 dedo (não é back gesture)

## Arquitetura

### Componentes Core (com/domino/input/)

- **GestureController.java** - Interface que define os callbacks de gestos
- **GestureListenerAdapter.java** - Classe abstrata para implementação fácil
- **InputManager.java** - Singleton que gerencia listeners e notificações

### Componentes Android (android/input/)

- **AndroidGestureProcessor.java** - InputProcessor que detecta gestos nativos do Android
- **GestureHandler.java** - Alternativa usando View (não utilizada atualmente)

## Como Usar

### Opção 1: Implementar GestureController diretamente (Recomendado)

```java
public class MyGameScreen extends BaseScreen implements GestureController {
  
  private OrthographicCamera camera;
  private float zoomLevel = 1.0f;
  private Vector2 cameraPos = new Vector2();

  @Override
  public void onBackGestureDetected() {
    Gdx.app.log("Game", "Voltando para menu");
    ((Game) Gdx.app.getApplicationListener()).setScreen(new MenuScreen());
  }

  @Override
  public void onPinchZoom(float scaleFactor) {
    zoomLevel *= scaleFactor;
    // Limitar zoom entre 0.5x e 3.0x
    zoomLevel = Math.max(0.5f, Math.min(3.0f, zoomLevel));
    camera.zoom = zoomLevel;
  }

  @Override
  public void onTwoFingerDrag(float deltaX, float deltaY) {
    // Mover câmera (invertido para parecer natural)
    cameraPos.x -= deltaX * camera.zoom;
    cameraPos.y += deltaY * camera.zoom;
    camera.position.set(cameraPos.x, cameraPos.y, 0);
  }

  @Override
  public void onSingleFingerDrag(float deltaX, float deltaY) {
    // Interações com UI ou mecânicas do jogo
  }
}
```

### Opção 2: Usar composição com GestureListenerAdapter

```java
public class MyGameScreen extends BaseScreen {
  private CameraController cameraController;

  public MyGameScreen() {
    super();
    cameraController = new CameraController();
    inputManager.addGestureListener(cameraController);
  }

  @Override
  public void dispose() {
    inputManager.removeGestureListener(cameraController);
    super.dispose();
  }

  private class CameraController extends GestureListenerAdapter {
    @Override
    public void onPinchZoom(float scaleFactor) {
      // implementação
    }
  }
}
```

## Detalhes de Implementação

### Back Gesture

```
[Borda Esquerda]
     |<--- 50px --->| [Toque aqui]
     |
     |------ arraste 100px+ para direita ----->
```

- Toque deve estar a menos de 50px da borda esquerda
- Arrastar mínimo de 100px para a direita
- Deve ser completado em menos de 500ms
- Dispara apenas uma vez por gesto

### Pinch Zoom

```
    Dois dedos se afastam
         / \
        /   \     scaleFactor > 1.0 → zoom in
    Dois dedos se aproximam
        \ /
         V      scaleFactor < 1.0 → zoom out
```

### Two-Finger Pan

```
    Dedo 1 →
    Dedo 2 →
    
    Movimento de pan é calculado pelo movimento dos 2 dedos juntos
```

## Configuração Avançada

Para ajustar sensibilidade dos gestos, edite as constantes em `AndroidGestureProcessor.java`:

```java
private static final float BACK_GESTURE_THRESHOLD = 100f;     // Distância mínima (px)
private static final float BACK_GESTURE_EDGE = 50f;           // Distância da borda (px)
private static final float DRAG_THRESHOLD = 10f;              // Threshold de movimento (px)
private static final long BACK_GESTURE_TIMEOUT = 500;         // Timeout máximo (ms)
```

## Fluxo de Detecção

```
User Touch Event
        ↓
AndroidLauncher.onTouchEvent()
        ↓
AndroidGestureProcessor.onTouchEvent()
        ↓
Análise de Gestos:
  - Back Gesture?
  - Pinch Zoom?
  - Two-Finger Drag?
  - Single-Finger Drag?
        ↓
InputManager.notifyXxx()
        ↓
Todas as telas registradas como listeners
        ↓
GestureController.onXxx()
        ↓
Implementação na tela
```

## Thread Safety

- **InputManager** é um Singleton thread-safe
- **Listeners** são notificados na thread principal do libGDX
- Pode-se adicionar/remover listeners a qualquer momento

## Exemplo Completo: Câmera Interativa

```java
public class InteractiveCameraScreen extends BaseScreen implements GestureController {
  
  private OrthographicCamera camera;
  private float baseZoom = 1.0f;
  private Vector2 cameraTarget = new Vector2();
  private Vector2 cameraPos = new Vector2();
  private static final float PAN_SPEED = 1.0f;
  private static final float MIN_ZOOM = 0.5f;
  private static final float MAX_ZOOM = 3.0f;

  public InteractiveCameraScreen() {
    super();
    camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    camera.position.set(0, 0, 0);
  }

  @Override
  public void onBackGestureDetected() {
    Gdx.app.log("Camera", "Back gesture!");
    ((Game) Gdx.app.getApplicationListener()).setScreen(new MenuScreen());
  }

  @Override
  public void onPinchZoom(float scaleFactor) {
    float newZoom = camera.zoom * (1.0f / scaleFactor);
    camera.zoom = Math.max(MIN_ZOOM, Math.min(MAX_ZOOM, newZoom));
  }

  @Override
  public void onTwoFingerDrag(float deltaX, float deltaY) {
    camera.position.x -= deltaX * PAN_SPEED;
    camera.position.y += deltaY * PAN_SPEED;
  }

  @Override
  public void onSingleFingerDrag(float deltaX, float deltaY) {
    // Pode ser usado para interações com elementos da UI
  }

  @Override
  public void render(float delta) {
    camera.update();
    super.render(delta);
  }
}
```

## Troubleshooting

### Gestos não disparam
1. Verificar se `show()` é chamado em BaseScreen
2. Garantir que a tela implementa `GestureController`
3. Confirmar que `super.show()` é chamado se sobrescrever

### Back gesture muito sensível
- Aumentar `BACK_GESTURE_THRESHOLD` para 150+
- Aumentar `BACK_GESTURE_EDGE` para 75px

### Pinch zoom muito rápido
```java
@Override
public void onPinchZoom(float scaleFactor) {
  // Suavizar com fator de 0.5
  float newZoom = camera.zoom * (1.0f / Gdx.math.lerp(1.0f, scaleFactor, 0.5f));
  // ...
}
```

### Dois gestos detectados simultaneamente
- Implementar lógica de prioridade no seu listener
- Por exemplo, ignorar single-finger drag se pinch zoom ativo

## Limitações Conhecidas

1. **Apenas Android**: Gestos não funcionam em desktop (LWJGL3)
2. **Uma câmera por tela**: Sistema assume uma única câmera por tela
3. **Sem rotação**: Gesto de rotação de dois dedos não implementado atualmente

## Próximas Melhorias Sugeridas

- [ ] Adicionar gesto de rotação (dois dedos girando)
- [ ] Suporte a edge swipe em outras bordas (direita, topo, baixo)
- [ ] Detector de long press
- [ ] Swipe gestures (rápido vs. arraste)
- [ ] Implementação em LWJGL3 com mouse

## Referências

- [Android Gesture Detection](https://developer.android.com/training/gestures)
- [libGDX InputProcessor](https://libgdx.com/wiki/input/event-handling)
- [ScaleGestureDetector](https://developer.android.com/reference/android/view/ScaleGestureDetector)
- [GestureDetector](https://developer.android.com/reference/android/view/GestureDetector)
