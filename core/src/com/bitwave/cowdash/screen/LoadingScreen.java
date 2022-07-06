package com.bitwave.cowdash.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.bitwave.cowdash.utils.Assets;
import com.bitwave.cowdash.utils.AudioUtils;
import com.bitwave.cowdash.utils.BackButtonAlternatives;

public class LoadingScreen extends BaseScreen {

    private final ScreenType screenType;
    private Stage stage;
    private boolean callBackSent;

    private byte levelIndex;
    private Image whiteCanvasImage;
    private Image loadingProgressImage;
    private Image loadingFrameImage;
    private Image keyart;

    public LoadingScreen(Game game, ScreenType screen) {
        super(game);
        Gdx.input.setInputProcessor(getCustomizedInput(BackButtonAlternatives.NONE));
        this.screenType = screen;
        switch (screen) {
            case SPLASH_SCREEN:
                Assets.getInstance().loadAudio();
                Assets.getInstance().loadMenuAssets();
                Assets.getInstance().loadPixmaps();
                break;
            case LEVEL_SELECT:
            case MY_COW:
            case CREDITS:
                Assets.getInstance().unloadInGameAssets();
                Assets.getInstance().loadMenuAssets();
                break;
            default:
                break;
        }

        this.setupStage();
    }

    public LoadingScreen(Game game, ScreenType screen, byte levelIndex) {
        super(game);
        Gdx.input.setInputProcessor(getCustomizedInput(BackButtonAlternatives.NONE));
        this.levelIndex = levelIndex;
        this.screenType = screen;

        switch (screen) {
            case GAME_SCREEN:
                Assets.getInstance().unloadMenuAssets();
                Assets.getInstance().loadInGameAssets();
                break;
            default:
                break;
        }

        this.setupStage();
    }

    @Override
    public void render(float delta) {
        stage.draw();
        tick(delta);
    }

    public void tick(float delta) {
        stage.act(delta);
        if (Assets.getInstance().isLoading()) {
            loadingProgressImage.setWidth((Assets.getInstance().getProgress() * 1.99f));
        } else {
            if (!callBackSent) {
                fadeOutScreen(screenType);
                callBackSent = true;
            }
        }
    }

    @Override
    public void hide() {
        super.hide();
        stage.dispose();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        this.stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
        super.pause();
        AudioUtils.getInstance().stopAllMusic();
    }

    @Override
    public void resume() {
    }

    private void setupStage() {
        this.stage = new Stage(new FillViewport(VIEWPORT_WIDTH, VIEWPORT_HEIGHT));
        Stack root = new Stack();
        root.setFillParent(true);
        stage.addActor(root);

        this.whiteCanvasImage = new Image(new Texture(Gdx.files.internal("sprites/in/whitetexture.png")));
        this.whiteCanvasImage.setColor(1, 1, 1, 1);
        this.whiteCanvasImage.addAction(Actions.fadeOut(1.2f));

        Texture keyartTexture = new Texture(Gdx.files.internal("sprites/backgrounds/keyart.png"));
        keyartTexture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        this.keyart = new Image(keyartTexture);

        this.loadingFrameImage = new Image(new Texture(Gdx.files.internal("sprites/objects/health_bar_2.png")));
        this.loadingProgressImage = new Image(new Texture(Gdx.files.internal("sprites/in/whitetexture.png")));
        this.loadingProgressImage.setColor(Color.GREEN);

        Container<Image> loadingProgressImageContainer = new Container<>(loadingProgressImage).padTop(3f).padBottom(3f).fill();

        Stack loadingBarStack = new Stack();
        loadingBarStack.add(loadingProgressImageContainer);
        loadingBarStack.add(loadingFrameImage);

        Table loadingBarTable = new Table();
        loadingBarTable.add(loadingBarStack).expand().padTop(108f);

        root.add(keyart);
        root.add(loadingBarTable);
        root.add(whiteCanvasImage);
    }

    private void fadeOutScreen(final ScreenType screenType) {
        whiteCanvasImage.addAction(Actions.sequence(Actions.fadeIn(.66f), Actions.run(new Runnable() {
            @Override
            public void run() {
                switch (screenType) {
                    case SPLASH_SCREEN:
                        AudioUtils.getInstance();
                        game.setScreen(new SplashScreen(game));
                        break;
                    case LEVEL_SELECT:
                        game.setScreen(new LevelSelectScreen(game, true));
                        break;
                    case MY_COW:
                        game.setScreen(new MyCowScreen(game, true));
                        break;
                    case GAME_SCREEN:
                        game.setScreen(new GameScreen(game, levelIndex));
                        break;
                    case CREDITS:
                        game.setScreen(new CreditsScreen(game, false));
                        break;
                    default:
                        break;
                }
            }
        })));
    }

}
