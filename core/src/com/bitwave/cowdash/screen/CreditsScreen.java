package com.bitwave.cowdash.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.bitwave.cowdash.objects.AICow;
import com.bitwave.cowdash.objects.item.Mask;
import com.bitwave.cowdash.utils.Assets;
import com.bitwave.cowdash.utils.AudioUtils;
import com.bitwave.cowdash.utils.BackButtonAlternatives;
import com.bitwave.cowdash.utils.ItemUtils;
import com.bitwave.cowdash.utils.ParticleHelper;
import com.bitwave.cowdash.utils.persistance.CowPreferences;
import com.bitwave.cowdash.utils.ui.AnimatedActor;
import com.bitwave.cowdash.utils.ui.ScrollingImage;

public class CreditsScreen extends MenuBaseScreen {

    private final boolean arrivingFromOptions;
    private final Image blackCanvas;
    private final AnimatedActor sunsetActor;
    private final ScrollingImage scrollingTiledImage;
    private final AICow blackCow;
    private final Label credits;
    private boolean itemCanNowBeUnlocked;

    public CreditsScreen(Game game, boolean arrivingFromOptions) {
        super(game, BackButtonAlternatives.BACK_TO_MENU);
        this.arrivingFromOptions = arrivingFromOptions;

        this.stage = new Stage(new FillViewport(VIEWPORT_WIDTH, VIEWPORT_HEIGHT));
        InputMultiplexer multiplexer = new InputMultiplexer(stage, backButtonProcessor);
        Gdx.input.setInputProcessor(multiplexer);

        this.blackCanvas = new Image(skin.getDrawable("whiterect"));
        this.blackCanvas.setColor(0, 0, 0, 0);
        this.blackCanvas.setBounds(0, 0, worldWidth, worldHeight);
        this.blackCanvas.addAction(Actions.fadeOut(.6f));

        this.sunsetActor = new AnimatedActor(new TextureRegion((Texture) Assets.getInstance().get("sprites/in/sunset_pink_strip2.png")), .2f, 400, 240, true);
        Table sunsetActorTable = new Table();
        sunsetActorTable.setFillParent(true);
        sunsetActorTable.add(sunsetActor).expand().fill();
        this.scrollingTiledImage = new ScrollingImage("sprites/backgrounds/wardrobe-tile.png",
                .3f, worldWidth);
        this.blackCow = new AICow(centerX, 72, 32, 32, true);

        this.blackCow.setColor(Color.BLACK);
        BitmapFont bitmapFont_12 = (BitmapFont) Assets.getInstance().get("fonts/cowfont_12.fnt");
        this.credits = new Label(
                " Producer And Game Designer \n" +
                        "**********************************\n" +
                        "            Niklas Istenes        \n\n" +
                        "             Programmers          \n" +
                        "**********************************\n" +
                        "            Johan Fredin          \n" +
                        "           Gustav Idström         \n" +
                        "           Niklas Istenes         \n" +
                        "            Olof Karlsson         \n\n" +
                        "             Graphics             \n" +
                        "**********************************\n" +
                        "        Christopher Andreasson    \n" +
                        "          Philipe Neguembor       \n" +
                        "           Ellinore Rönning       \n" +
                        "         Jörgen Alexandersson     \n\n" +
                        "              Music               \n" +
                        "**********************************\n" +
                        "           Pontus Lundén          \n" +
                        "        Alexander Paunovic        \n" +
                        "            Niklas Ivanov         \n\n" +
                        "         Special Thanks To        \n" +
                        "**********************************\n" +
                        "              Chuan Su            \n" +
                        "             Tony Ivanov          \n" +
                        "              Alicia Svensson             \n" +
                        "              Carl Essunger             \n" +
                        "              Madeleine Essunger             \n" +
                        "              Ragnar Axberg Svensson             \n" +
                        "				Björn Nilsson				\n" +
                        "				Tracy Wang						\n" +
                        "				Evelyn Hernandez 			\n" +
                        "\n\n\n\n" +
                        "        Thank you for playing!      ", new LabelStyle(bitmapFont_12, Color.BLACK));

        this.setInitialPositions();
        this.addActorsToStage(sunsetActorTable, credits, scrollingTiledImage, blackCow, blackCanvas);
    }

    private void setInitialPositions() {
        scrollingTiledImage.setPosition(0, -72);
        scrollingTiledImage.setSpriteColor(Color.BLACK);
        credits.setAlignment(com.badlogic.gdx.utils.Align.center);
        credits.setPosition(centerX - credits.getPrefWidth() / 2, -credits.getPrefHeight() / 1.05f);
        credits.addAction(Actions.sequence(Actions.moveBy(0, worldHeight * 3.68f, 35f), Actions.run(new Runnable() {
            @Override
            public void run() {
                itemCanNowBeUnlocked = true;
            }
        })));

        this.stage.addListener(new InputListener() {

            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                return true;
            }

            @Override
            public boolean keyUp(InputEvent event, int keycode) {
                return true;
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                stage.removeListener(this);
                blackCanvas.addAction(Actions.sequence(Actions.fadeIn(.6f), Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        if (itemCanNowBeUnlocked && !CowPreferences.getInstance().isItemUnlocked(ItemUtils.MASK, Mask.TIKI_MASK.getId())) {
                            AudioUtils.getInstance().crossFadeTracks(nameOfTrackToPlay, "boss", 10f, true);
                            game.setScreen(new ItemUnlockedScreen(game, arrivingFromOptions));
                        } else {
                            AudioUtils.getInstance().crossFadeTracks(nameOfTrackToPlay, "menu", 10f, true);
                            if (!arrivingFromOptions) {
                                game.setScreen(new MainMenuScreen(game, true));
                            } else {
                                game.setScreen(new OptionsScreen(game, true));
                            }
                        }
                    }
                })));
                return true;
            }
        });
    }

    @Override
    public void tick(float deltaTime) {
        stage.act(deltaTime);
        ParticleHelper.getInstance().tick(deltaTime, true);
    }


}
