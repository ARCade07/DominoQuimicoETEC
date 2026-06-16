# Gesture Handling no Android

Sistema de detecção de gestos do Android integrado com libGDX para o jogo ChemDom.

## Gestos Suportados

### 1. Back Gesture (Arrastar no canto para voltar)
- **Detecção**: Toque perto da borda esquerda (50dp) + arraste para a direita (100dp+)
- **Timeout**: 500ms
- **Callback**: `onBackGestureDetected()`

```java
@Override
public void onBackGestureDetected() {
  // Voltar para a tela anterior
  ((Game) Gdx.app.getApplicationListener()).setScreen(previousScreen);
}
```

### 2. Pinch Zoom (Pinçar para controlar zoom)
- **Detecção**: Dois dedos se afastando ou aproximando
- **Callback**: `onPinchZoom(float scaleFactor)`
- **Scale Factor**: > 1.0 para zoom in, < 1.0 para zoom out

```java
private float currentZoom = 1.0f;

@Override
public void onPinchZoom(float scaleFactor) {
  currentZoom *= scaleFactor;
  currentZoom = Math.max(0.5f, Math.min(3.0f, currentZoom)); // Limitar zoom
  camera.zoom = currentZoom;
}
```

### 3. Two-Finger Pan (Arrastar com dois dedos)
- **Detecção**: Dois dedos se movendo juntos na mesma direção
- **Callback**: `onTwoFingerDrag(float deltaX, float deltaY)`
- **Unidades**: Pixels de movimento

```java
private float cameraX = 0;
private float cameraY = 0;

@Override
public void onTwoFingerDrag(float deltaX, float deltaY) {
  cameraX -= deltaX * camera.zoom; // Inversão do movimento para parecer natural
  cameraY += deltaY * camera.zoom;
  camera.position.set(cameraX, cameraY, 0);
}
```

### 4. Single Finger Drag (Arrastar com um dedo)
- **Detecção**: Um dedo se movendo (quando não é back gesture)
- **Callback**: `onSingleFingerDrag(float deltaX, float deltaY)`

```java
@Override
public void onSingleFingerDrag(float deltaX, float deltaY) {
  // Usar para UI interactions ou other drag-based mechanics
}
```

## Implementação em uma Screen

### Opção 1: Estender GestureListenerAdapter

```java
public class MyScreen extends BaseScreen implements GestureController {

  @Override
  public void onBackGestureDetected() {
    Gdx.app.log("Gesture", "Back gesture detected");
  }

  @Override
  public void onPinchZoom(float scaleFactor) {
    Gdx.app.log("Gesture", "Pinch zoom: " + scaleFactor);
  }

  @Override
  public void onTwoFingerDrag(float deltaX, float deltaY) {
    Gdx.app.log("Gesture", "Two-finger drag: " + deltaX + ", " + deltaY);
  }

  @Override
  public void onSingleFingerDrag(float deltaX, float deltaY) {
    Gdx.app.log("Gesture", "Single finger drag: " + deltaX + ", " + deltaY);
  }
}
```

### Opção 2: Usar GestureListenerAdapter

```java
public class MyScreen extends BaseScreen {
  private MyGestureListener gestureListener;

  public MyScreen() {
    super();
    gestureListener = new MyGestureListener();
    inputManager.addGestureListener(gestureListener);
  }

  @Override
  public void dispose() {
    inputManager.removeGestureListener(gestureListener);
    super.dispose();
  }

  private class MyGestureListener extends GestureListenerAdapter {
    @Override
    public void onPinchZoom(float scaleFactor) {
      // implementação
    }
  }
}
```

## Constantes de Configuração

No arquivo `AndroidGestureProcessor.java`:

- `BACK_GESTURE_THRESHOLD`: Distância mínima em pixels para detectar back gesture (padrão: 100px)
- `BACK_GESTURE_EDGE`: Distância da borda esquerda para iniciar back gesture (padrão: 50px)
- `BACK_GESTURE_TIMEOUT`: Tempo máximo em ms para completar back gesture (padrão: 500ms)
- `DRAG_THRESHOLD`: Movimento mínimo para ativar drag listeners (padrão: 10px)

## Notas Importantes

1. **Compatibilidade**: Funciona apenas na plataforma Android
2. **BaseScreen**: A integração automática ocorre em `BaseScreen.show()` para qualquer tela que implemente `GestureController`
3. **Cleanup**: Garanta chamar `super.dispose()` para remover listeners ao descartar telas
4. **Thread Safety**: InputManager é thread-safe através de singleton

## Exemplo Completo

```java
public class GameScreen extends BaseScreen implements GestureController {
  private OrthographicCamera camera;
  private float zoomLevel = 1.0f;

  public GameScreen() {
    super();
    camera = new OrthographicCamera();
  }

  @Override
  public void onBackGestureDetected() {
    Gdx.app.log("Game", "Returning to menu");
    ((Game) Gdx.app.getApplicationListener()).setScreen(new MenuScreen());
  }

  @Override
  public void onPinchZoom(float scaleFactor) {
    zoomLevel *= scaleFactor;
    zoomLevel = Math.max(0.5f, Math.min(3.0f, zoomLevel));
    camera.zoom = zoomLevel;
  }

  @Override
  public void onTwoFingerDrag(float deltaX, float deltaY) {
    camera.position.x -= deltaX * camera.zoom;
    camera.position.y += deltaY * camera.zoom;
  }

  @Override
  public void render(float delta) {
    camera.update();
    super.render(delta);
  }
}
```

## Troubleshooting

### Gestos não funcionam
1. Certifique-se de que sua tela implementa `GestureController`
2. Verifique se `show()` é chamado (ou chame `super.show()`)
3. Confirme que os métodos não estão vazios

### Back gesture não funciona
1. Aumentar `BACK_GESTURE_THRESHOLD` se o gesto for muito sensível
2. Verificar se toque está perto da borda esquerda (< 50px)
3. Validar timeout (deve ser < 500ms)

### Zoom muito rápido/lento
- Ajustar o `scaleFactor` multiplicando por constante: `onPinchZoom(scaleFactor * 0.5f)`
