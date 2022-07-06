package com.bitwave.cowdash.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.bitwave.cowdash.utils.Assets;
import com.bitwave.cowdash.utils.AudioUtils;
import com.bitwave.cowdash.utils.BackButtonAlternatives;
import com.bitwave.cowdash.utils.CustomButton;
import com.bitwave.cowdash.utils.ParticleHelper;
import com.bitwave.cowdash.utils.ui.CustomInterpolations;

public class MainMenuScreen extends MenuBaseScreen {

    private final byte PLAY = 0;
    private final byte MY_COW = 1;
    private final byte OPTIONS = 2;
    private final byte LOGO = 3;
    private final Image whiteCanvasImage;

    private final Array<CustomButton> buttons = new Array<CustomButton>();
    private Image titleImage;
    private CustomButton playButton;
    private CustomButton myCowButton;
    private CustomButton optionsButton;
    private Image wunderlingPromoImage;


    public MainMenuScreen(Game game, boolean shouldPlayMusic) {
        super(game, BackButtonAlternatives.QUIT);
        AudioUtils.getInstance().playMusic("menu", true, shouldPlayMusic);

        this.whiteCanvasImage = new Image(skin.getDrawable("whiterect"));
        this.whiteCanvasImage.setBounds(0, 0, worldWidth, worldHeight);
        if (shouldPlayMusic) {
            this.whiteCanvasImage.addAction(Actions.sequence(Actions.fadeOut(.45f), Actions.run(new Runnable() {
                @Override
                public void run() {
                    whiteCanvasImage.setVisible(false);
                }
            })));
        } else {
            this.whiteCanvasImage.setVisible(false);
            this.whiteCanvasImage.setColor(1f, 1f, 1f, 0f);
        }

        this.stage.addActor(whiteCanvasImage);

        this.initButtonsAndImages(worldWidth, worldHeight, shouldPlayMusic);
        this.setButtonListener(playButton, PLAY);
        this.setButtonListener(myCowButton, MY_COW);
        this.setButtonListener(optionsButton, OPTIONS);
        this.setButtonListener(titleImage, LOGO);

        buttons.add(playButton);
        buttons.add(myCowButton);
        buttons.add(optionsButton);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
    }


    @Override
    public void tick(float delta) {
        stage.act(delta);
        ParticleHelper.getInstance().tick(delta, true);
    }

    private void initButtonsAndImages(float width, float height, boolean fadeOutWhiteCanvas) {
        Stack rootStack = new Stack();
        rootStack.setFillParent(true);
        addActorsToStage(rootStack);

        Table rootTable = new Table();
        rootTable.setFillParent(true);
        rootStack.add(rootTable);

        this.titleImage = new Image(skin.getDrawable("cow_dash_logo"));
        this.playButton = new CustomButton(skin.getDrawable("start_menu_play_normal"), skin.getDrawable("start_menu_play_pressed"));
        this.myCowButton = new CustomButton(skin.getDrawable("start_menu_my_cow_normal"), skin.getDrawable("start_menu_my_cow_pressed"));
        this.optionsButton = new CustomButton(skin.getDrawable("start_menu_options"), skin.getDrawable("start_menu_optionspressed"));

        Table innerTable = new Table();
        innerTable.add(this.titleImage).center().padTop(-16f).padBottom(-4f).row();
        innerTable.add(this.playButton).center().padBottom(4f).row();
        innerTable.add(this.myCowButton).center().padBottom(4f).row();
        innerTable.add(this.optionsButton).center();
        rootTable.add(innerTable).align(Align.center);

        Texture wunderlingPromoTexture = (Texture) Assets.getInstance().get("sprites/ui/wunderling_promo.png");
        wunderlingPromoTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        this.wunderlingPromoImage = new Image(wunderlingPromoTexture);
        wunderlingPromoImage.setOrigin(Align.center);

        Actor wunderlingPromoInvisibleButtonActor = new Actor();
        wunderlingPromoInvisibleButtonActor.setSize(72f, 72f);
        wunderlingPromoInvisibleButtonActor.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (getTapCount() <= 1) {
                    stage.addAction(Actions.delay(0.35f, Actions.run(new Runnable() {
                        @Override
                        public void run() {
                            Gdx.net.openURI("https://www.youtube.com/watch?v=w2kX9RSOi78");
                        }
                    })));
                    ParticleHelper.getInstance().addWoodenEffect(event.getStageX(), event.getStageY(), true);
                }
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                boolean result = super.touchDown(event, x, y, pointer, button);
                if (result) {
                    wunderlingPromoImage.addAction(Actions.scaleBy(-0.2f, -0.2f, 0.1f, CustomInterpolations.swingOut));
                } else {
                    cancel();
                }
                return result;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                wunderlingPromoImage.addAction(Actions.scaleTo(1f, 1f, 0.35f, CustomInterpolations.swingOut));
            }
        });

        Container<Actor> wunderlingPromoButtonContainer = new Container<>(wunderlingPromoInvisibleButtonActor);
        wunderlingPromoButtonContainer.pad(16f);

        Stack wunderlingPromoButtonStack = new Stack();
        wunderlingPromoButtonStack.add(wunderlingPromoImage);
        wunderlingPromoButtonStack.add(wunderlingPromoButtonContainer);

        Table wunderlingPromoTable = new Table();
        wunderlingPromoTable.add(wunderlingPromoButtonStack).expand().bottom().right();
        rootStack.add(wunderlingPromoTable);

        rootStack.validate();

        this.titleImage.addAction(Actions.sequence(Actions.moveTo(titleImage.getX(), titleImage.getY(), 0.8f, CustomInterpolations.swingOut)));
        this.playButton.addAction(Actions.sequence(Actions.delay(0.2f), Actions.moveTo(playButton.getX(), playButton.getY(), 0.35f, CustomInterpolations.swingOut)));
        this.myCowButton.addAction(Actions.sequence(Actions.delay(.3f), Actions.moveTo(myCowButton.getX(), myCowButton.getY(), 0.35f, CustomInterpolations.swingOut)));
        this.optionsButton.addAction(Actions.sequence(Actions.delay(0.4f), Actions.moveTo(optionsButton.getX(), optionsButton.getY(), 0.35f, CustomInterpolations.swingOut), Actions.run(new Runnable() {

            @Override
            public void run() {

            }
        })));
        this.wunderlingPromoImage.addAction(
                Actions.delay(0.75f,
                        Actions.parallel(
                                Actions.fadeIn(0.35f),
                                Actions.scaleTo(1f, 1f, 0.35f, CustomInterpolations.swingOut)
                        )
                )
        );
        this.wunderlingPromoImage.addAction(Actions.forever(
                Actions.sequence(
                        Actions.delay(2.5f),
                        Actions.rotateTo(3f, 0.15f, CustomInterpolations.swingOut),
                        Actions.rotateTo(-3f, 0.15f, CustomInterpolations.swingOut),
                        Actions.rotateTo(0, 0.2f, CustomInterpolations.swingOut)
                )
        ));

        this.titleImage.setX(-width - titleImage.getPrefWidth());
        this.playButton.setY(-worldHeight * 0.66f);
        this.myCowButton.setY(-worldHeight * 0.66f);
        this.optionsButton.setY(-worldHeight * 0.66f);
        this.wunderlingPromoImage.setScale(0f);
    }

    private void setButtonListener(final Actor button, final byte ACTION) {
        if (button != null) {
            button.addListener(new InputListener() {

                @Override
                public boolean keyDown(InputEvent event, int keycode) {
                    return true;
                }

                @Override
                public boolean keyUp(InputEvent event, int keycode) {
                    return true;
                }

                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int b) {
                    return true;
                }

                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    if (event.getListenerActor().hit(x, y, true) != null) {
                        ParticleHelper.getInstance().addWoodenEffect(event.getStageX(), event.getStageY(), true);
                        switch (ACTION) {
                            case PLAY:
                            case MY_COW:
                            case OPTIONS:
                                fadeOutAndSwitchScreen(ACTION);
                                break;
                            case LOGO:
                                AudioUtils.getInstance().playSoundFX("mooh");
                                break;
                            default:
                                break;
                        }
                    }
                }
            });
        }
    }

    private void fadeOutAndSwitchScreen(final byte actionToPerformOnceFinished) {
        AudioUtils.getInstance().playSoundFX("button");
        disableActors();
        this.titleImage.addAction(Actions.moveTo(worldWidth, titleImage.getY(), .5f, CustomInterpolations.swingIn));

        this.optionsButton.addAction(Actions.sequence(Actions.delay(.1f), Actions.moveTo(optionsButton.getX(), -worldHeight * 0.66f, .5f, Interpolation.exp5Out)));
        this.myCowButton.addAction(Actions.sequence(Actions.delay(.2f), Actions.moveTo(myCowButton.getX(), -worldHeight * 0.66f, .5f, Interpolation.exp5Out)));
        this.playButton.addAction(Actions.sequence(Actions.delay(.3f), Actions.moveTo(playButton.getX(), -worldHeight * 0.66f, .5f, Interpolation.exp5Out),
                Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        switch (actionToPerformOnceFinished) {
                            case PLAY:
                                saveUValuesForParallax();
                                game.setScreen(new WorldSelectScreen(game, false));
                                break;
                            case OPTIONS:
                                saveUValuesForParallax();
                                game.setScreen(new OptionsScreen(game, false));
                                break;
                            case MY_COW:
                                saveUValuesForParallax();
                                game.setScreen(new MyCowScreen(game, false));
                                break;
                        }

                    }
                })));

        wunderlingPromoImage.clearActions();
        wunderlingPromoImage.addAction(
                Actions.delay(0.f,
                        Actions.parallel(
                                Actions.fadeOut(0.1f),
                                Actions.scaleTo(0f, 0f, 0.25f, Interpolation.exp5Out)
                        )
                )
        );
    }

}