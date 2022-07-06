package com.bitwave.cowdash.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;
import com.bitwave.cowdash.objects.AICow;
import com.bitwave.cowdash.objects.item.Mask;
import com.bitwave.cowdash.utils.AudioUtils;
import com.bitwave.cowdash.utils.BackButtonAlternatives;
import com.bitwave.cowdash.utils.ItemUtils;
import com.bitwave.cowdash.utils.language.Translator;
import com.bitwave.cowdash.utils.persistance.CowPreferences;
import com.bitwave.cowdash.utils.ui.AnimatedActor;
import com.bitwave.cowdash.utils.ui.MovingStar;

public class ItemUnlockedScreen extends MenuBaseScreen {

    private final Mask tikiMask = Mask.TIKI_MASK;
    private final Image blackCanvas;

    private final boolean arrivingFromOptions;
    private AICow blackCow;

    public ItemUnlockedScreen(Game game, boolean arrivingFromOptions) {
        super(game, BackButtonAlternatives.OPTIONS);
        this.arrivingFromOptions = arrivingFromOptions;
        this.showItemUnlocked();

        this.blackCanvas = new Image(skin.getDrawable("whiterect"));
        this.blackCanvas.setBounds(0, 0, worldWidth, worldHeight);
        this.blackCanvas.setColor(0, 0, 0, 0);
        this.stage.addActor(blackCanvas);
    }

    @Override
    public void tick(float deltaTime) {
        stage.act(deltaTime);
    }

    private void showItemUnlocked() {
        float width = centerX;
        AudioUtils.getInstance().switchTrack("menu", "boss");
        AudioUtils.getInstance().playSoundFX("mooh");

        final Image blackBackGroundImage = new Image(skin.getDrawable("whiterect"));
        final Image whiteBannerImage = new Image(skin.getDrawable("unlock_screen_bw"));

        String[] sparkleType = {
                "sparkly_sparkle_red_16x16_strip8",
                "sparkly_sparkle_yellow_16x16_strip8",
                "sparkly_sparkle_green_16x16_strip8"
        };
        Array<MovingStar> sparkleActors = new Array<MovingStar>();
        float x = 0;
        float y = 16f;
        for (byte i = 0; i < 20; i++) {
            sparkleActors.add(new MovingStar(skin.getRegion(sparkleType[MathUtils.random(2)]), MathUtils.random(.025f, .1f), x, y, 16, 16, 600f));
            sparkleActors.get(i).setPosition(x, y);
            x += 32;
            y += 32;
            if (x > worldWidth) {
                x = worldWidth;
            }
            if (y > worldHeight) {
                y = 16;
            }
        }

        blackBackGroundImage.setColor(0, 0, 0, 1);
        blackBackGroundImage.setBounds(0, 0, worldWidth, worldHeight);

        final Label headerLabel = new Label(Translator.getInstance().t("unlocked"), labelStyle_16_whiteColor);
        final Label itemUnlockedLabel = new Label(tikiMask.toString() + "!", labelStyle_16_whiteColor);
        itemUnlockedLabel.addAction(Actions.repeat(RepeatAction.FOREVER, Actions.sequence(Actions.fadeOut(0.1f), Actions.delay(0.6f), Actions.fadeIn(0.1f), Actions.delay(0.6f))));
        headerLabel.addAction(Actions.repeat(RepeatAction.FOREVER, Actions.sequence(Actions.fadeOut(0.1f), Actions.delay(0.6f), Actions.fadeIn(0.1f), Actions.delay(0.6f))));

        headerLabel.setPosition(width - (headerLabel.getPrefWidth() / 2), getCorrectY(headerLabel, 76f));
        itemUnlockedLabel.setPosition(width - (itemUnlockedLabel.getPrefWidth() / 2), getCorrectY(itemUnlockedLabel, 162f));
        this.blackCow = new AICow(width - 16, (camera.viewportHeight / 2) - 16, ItemUtils.MASK, tikiMask.getId(), true, true);

        CowPreferences.getInstance().unlockItem(ItemUtils.MASK, tikiMask.getId());
        whiteBannerImage.setPosition(0, blackCow.getY() - 20);

        stage.addActor(blackBackGroundImage);
        for (AnimatedActor animatedActor : sparkleActors) {
            stage.addActor(animatedActor);
        }
        addActorsToStage(whiteBannerImage, blackCow, headerLabel, itemUnlockedLabel);


        stage.addListener(new InputListener() {


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
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                AudioUtils.getInstance().crossFadeTracks("boss", "menu", 10f, true);
                stage.removeListener(this);
                blackCanvas.addAction(Actions.sequence(Actions.fadeIn(.6f), Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        if (arrivingFromOptions) {
                            game.setScreen(new OptionsScreen(game, true));
                        } else {
                            game.setScreen(new MainMenuScreen(game, true));
                        }
                    }
                })));
            }
        });
    }

}
