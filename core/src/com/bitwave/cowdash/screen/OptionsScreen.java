package com.bitwave.cowdash.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;
import com.bitwave.cowdash.utils.AudioUtils;
import com.bitwave.cowdash.utils.BackButtonAlternatives;
import com.bitwave.cowdash.utils.CustomButton;
import com.bitwave.cowdash.utils.LabelFormatter;
import com.bitwave.cowdash.utils.ParticleHelper;
import com.bitwave.cowdash.utils.language.Translator;
import com.bitwave.cowdash.utils.persistance.CowPreferences;


public class OptionsScreen extends MenuBaseScreen {

    private final byte SOUND = 0;
    private final byte MUSIC = 1;
    private final byte VIBRATION = 2;
    private final byte CLEAR = 3;
    private final byte CREDITS = 4;
    private final byte BACK = 5;
    private final byte CONFIRM = 6;
    private final byte CANCEL = 7;
    private final byte PARTICLES = 8;
    private final CheckBox musicImage;
    private final CheckBox soundImage;
    private final CheckBox soundCheckBox;
    private final CheckBox musicCheckBox;
    private final CheckBox particleCheckBox;
    private final Image blackCanvas;
    private final Image optionsTopImage;
    private final Image optionsBottomImage;
    private final CustomButton clearSaveDataButton;
    private final CustomButton creditsButton;
    private final CustomButton returnToMenuButton;
    private final Array<CustomButton> buttons = new Array<CustomButton>();
    private boolean isCreditsButtonPressed;
    private Actor[] confirmDialogActors;
    private CustomButton confirmButton;
    private CustomButton cancelButton;
    private CustomButton soundButton;
    private CustomButton musicButton;

    public OptionsScreen(Game game, boolean shouldPlayMusic) {
        super(game, BackButtonAlternatives.BACK_TO_MENU);

        AudioUtils.getInstance().resetVolume();
        if (shouldPlayMusic) {
            AudioUtils.getInstance().playMusic("menu");
        }

        CowPreferences cowPreferences = CowPreferences.getInstance();

        this.optionsTopImage = new Image(skin.getDrawable("options_top"));
        this.optionsBottomImage = new Image(skin.getDrawable("options_bottom"));

        this.musicImage = getCustomizedCheckBox("options_music_text", "options_music_text_normal", true, !cowPreferences.isMusicMute());
        this.soundImage = getCustomizedCheckBox("options_sound_text", "options_sound_text_normal", true, !cowPreferences.isSoundFXMute());
        this.particleCheckBox = getCustomizedCheckBox("options_fx_light", "options_fx_normal", true, cowPreferences.isParticlesEnabled());

        this.musicImage.setDisabled(true);
        this.soundImage.setDisabled(true);

        this.soundCheckBox = getCustomizedCheckBox("options_sound_light", "options_sound_normal", true, !cowPreferences.isSoundFXMute());
        this.musicCheckBox = getCustomizedCheckBox("options_music_light", "options_music_normal", true, !cowPreferences.isMusicMute());

        this.clearSaveDataButton = new CustomButton(skin.getDrawable("options_clear data_normal"), skin.getDrawable("options_clear data"));
        this.creditsButton = new CustomButton(skin.getDrawable("options_credits_normal"), skin.getDrawable("options_credits"));
        this.returnToMenuButton = new CustomButton(skin.getDrawable("options_back_to_menu_normal"), skin.getDrawable("options_back_to_menu"));

        buttons.add(creditsButton);
        buttons.add(returnToMenuButton);
        buttons.add(clearSaveDataButton);
        buttons.add(musicButton);
        buttons.add(soundButton);

        setInitialPositions();

        setButtonListener(clearSaveDataButton, CLEAR);
        setButtonListener(creditsButton, CREDITS);
        setButtonListener(returnToMenuButton, BACK);
        setButtonListener(particleCheckBox, PARTICLES);
        setButtonListener(soundCheckBox, SOUND);
        setButtonListener(musicCheckBox, MUSIC);


        animateStage(true);

        this.blackCanvas = new Image(skin.getDrawable("whiterect"));
        this.blackCanvas.setColor(0, 0, 0, 0);
        this.blackCanvas.setBounds(0, 0, worldWidth, worldHeight);

        addActorsToStage(optionsTopImage, optionsBottomImage, soundCheckBox, musicCheckBox, clearSaveDataButton, creditsButton, returnToMenuButton, musicImage, soundImage, particleCheckBox);
    }

    private void setInitialPositions() {
        this.optionsTopImage.setPosition(0, worldHeight);
        float optionsTopImageTopY = optionsTopImage.getY() + optionsTopImage.getPrefHeight() - 64;
        this.soundImage.setPosition(optionsTopImage.getX() + 64, optionsTopImageTopY - 32);
        this.soundCheckBox.setPosition(optionsTopImage.getX() + 224, optionsTopImageTopY - 32);
        this.musicImage.setPosition(optionsTopImage.getX() + 64, optionsTopImageTopY - 95);
        this.musicCheckBox.setPosition(optionsTopImage.getX() + 224, optionsTopImageTopY - 96);
        this.particleCheckBox.setPosition(optionsTopImage.getX() + 306, optionsTopImageTopY - 72);
        this.clearSaveDataButton.setPosition(optionsTopImage.getX() + 64, optionsTopImageTopY - 176);

        this.optionsBottomImage.setPosition(worldWidth - optionsBottomImage.getPrefWidth(), -optionsBottomImage.getPrefHeight());
        this.creditsButton.setPosition(optionsBottomImage.getX() + 2, 16 - optionsBottomImage.getPrefHeight());
        this.returnToMenuButton.setPosition(optionsBottomImage.getX() + 66, 48 - optionsBottomImage.getPrefHeight());
    }

    private void animateStage(final boolean down) {
        float delay = down ? .33f : 0.0f;
        float duration = 0.4f;
        Interpolation interpolation = down ? Interpolation.exp5Out : Interpolation.exp5In;

        float optionsTopImageTargetY = down ? worldHeight - optionsTopImage.getPrefHeight() : worldHeight;
        float optionsTopImageTargetTopY = optionsTopImageTargetY + optionsTopImage.getPrefHeight() - 64;
        this.optionsTopImage.addAction(Actions.sequence(Actions.delay(delay), Actions.moveTo(optionsTopImage.getX(), optionsTopImageTargetY, duration, interpolation)));
        this.musicImage.addAction(Actions.sequence(Actions.delay(delay), Actions.moveTo(musicImage.getX(), optionsTopImageTargetTopY - 95, duration, interpolation)));
        this.soundImage.addAction(Actions.sequence(Actions.delay(delay), Actions.moveTo(soundImage.getX(), optionsTopImageTargetTopY - 32, duration, interpolation)));
        this.soundCheckBox.addAction(Actions.sequence(Actions.delay(delay), Actions.moveTo(soundCheckBox.getX(), optionsTopImageTargetTopY - 32, duration, interpolation)));

        this.musicCheckBox.addAction(Actions.sequence(Actions.delay(delay), Actions.moveTo(musicCheckBox.getX(), optionsTopImageTargetTopY - 96, duration, interpolation)));
        this.particleCheckBox.addAction(Actions.sequence(Actions.delay(delay), Actions.moveTo(particleCheckBox.getX(), optionsTopImageTargetTopY - 72, duration, interpolation)));
        this.clearSaveDataButton.addAction(Actions.sequence(Actions.delay(delay), Actions.moveTo(clearSaveDataButton.getX(), optionsTopImageTargetTopY - 176, duration, interpolation)));

        this.optionsBottomImage.addAction(Actions.sequence(Actions.delay(delay), Actions.moveTo(optionsBottomImage.getX(), down ? 0 : -optionsBottomImage.getPrefHeight(), duration, interpolation)));
        this.creditsButton.addAction(Actions.sequence(Actions.delay(delay), Actions.moveTo(creditsButton.getX(), down ? 16 : 16 - optionsBottomImage.getPrefHeight(), duration, interpolation)));
        this.returnToMenuButton.addAction(Actions.sequence(Actions.delay(delay), Actions.sequence(Actions.moveTo(returnToMenuButton.getX(), down ? 48 : 48 - optionsBottomImage.getPrefHeight(), duration, interpolation), Actions.run(new Runnable() {
            @Override
            public void run() {
                if (!down && !isCreditsButtonPressed) {
                    saveUValuesForParallax();
                    game.setScreen(new MainMenuScreen(game, false));
                } else if (!down && isCreditsButtonPressed) {
                    stage.addActor(blackCanvas);
                    blackCanvas.addAction(Actions.sequence(Actions.fadeIn(.6f), Actions.run(new Runnable() {
                        @Override
                        public void run() {
                            AudioUtils.getInstance().crossFadeTracks("menu", nameOfTrackToPlay, 10f, false);
                            game.setScreen(new CreditsScreen(game, true));
                        }
                    })));
                }
            }
        }))));
    }

    @Override
    public void tick(float deltaTime) {
        stage.act(deltaTime);
        ParticleHelper.getInstance().tick(deltaTime, true);
    }

    private void setButtonListener(final Button buttonPassedIn, final int ACTION) {
        buttonPassedIn.addListener(new InputListener() {

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
                if (event.getListenerActor().hit(x, y, true) != null) {
                    ParticleHelper.getInstance().addWoodenEffect(event.getStageX(), event.getStageY(), true);
                    AudioUtils.getInstance().playSoundFX("button");
                    CowPreferences cowPreferences = CowPreferences.getInstance();

                    switch (ACTION) {
                        case SOUND:
                            CowPreferences.getInstance().toggleSoundFX(!buttonPassedIn.isChecked());
                            soundImage.setChecked(buttonPassedIn.isChecked());
                            break;
                        case MUSIC:
                            CowPreferences.getInstance().toggleMusic(!buttonPassedIn.isChecked());
                            AudioUtils.getInstance().playMusic("menu", true);
                            if (!buttonPassedIn.isChecked()) {
                                AudioUtils.getInstance().stopTrack("menu");
                            }
                            musicImage.setChecked(buttonPassedIn.isChecked());
                            break;
                        case VIBRATION:
                            CowPreferences.getInstance().toggleVibration(!buttonPassedIn.isChecked());
                            if (!buttonPassedIn.isChecked()) {
                                Gdx.input.vibrate(35);
                            }
                            break;
                        case PARTICLES:
                            cowPreferences.toggleParticles(!buttonPassedIn.isChecked());
                            break;
                        case CLEAR:
                            showConfirmClearSaveDataDialog();
                            break;
                        case CONFIRM:
                            removeSelectedActorsFromStage(confirmDialogActors);
                            cowPreferences.clearPreferences();
                            musicImage.setChecked(true);
                            if (!musicCheckBox.isChecked()) {
                                musicCheckBox.setChecked(true);
                                AudioUtils.getInstance().playMusic("menu", true);
                            }
                            soundImage.setChecked(true);
                            soundCheckBox.setChecked(true);
                            particleCheckBox.setChecked(cowPreferences.isParticlesEnabled());
                            break;
                        case CREDITS:
                            AudioUtils.getInstance().playSoundFX("button");
                            animateStage(false);
                            isCreditsButtonPressed = true;
                            break;
                        case CANCEL:
                            removeSelectedActorsFromStage(confirmDialogActors);
                            break;
                        case BACK:
                            isCreditsButtonPressed = false;
                            AudioUtils.getInstance().playSoundFX("button");
                            disableActors();
                            animateStage(false);
                            break;
                    }
                }
            }
        });
    }

    private void showConfirmClearSaveDataDialog() {
        if (confirmDialogActors == null) {
            Image dialogBoxImage = new Image(skin.getDrawable("dialog_box"));
            dialogBoxImage.setPosition(centerX - dialogBoxImage.getPrefWidth() / 2f, (worldHeight - dialogBoxImage.getPrefHeight()) / 2f);

            Image blackNastyImage = new Image(skin.getDrawable("whiterect"));
            blackNastyImage.setColor(0, 0, 0, 0.5f);
            blackNastyImage.setBounds(0, 0, worldWidth, worldHeight);
            String deleteDataString = Translator.getInstance().t("delete_data");
            LabelFormatter formatter = new LabelFormatter();
            Label confirmLabel = new Label(formatter.getFormattedString((byte) 16, deleteDataString), labelStyle_16_whiteColor);
            confirmButton = new CustomButton(skin.getDrawable("dialog_box_yes_normal"), skin.getDrawable("dialog_box_yes"));
            cancelButton = new CustomButton(skin.getDrawable("dialog_box_no_normal"), skin.getDrawable("dialog_box_no"));
            confirmLabel.setPosition(dialogBoxImage.getX() + ((dialogBoxImage.getPrefWidth() - confirmLabel.getPrefWidth()) / 2f) + 8f, dialogBoxImage.getY() + ((dialogBoxImage.getPrefHeight() - confirmLabel.getPrefHeight()) / 2f) + 7f);
            confirmButton.setPosition(dialogBoxImage.getX() + 288, dialogBoxImage.getY() + 16f);
            cancelButton.setPosition(dialogBoxImage.getX() + 64, dialogBoxImage.getY() + 16f);
            setButtonListener(confirmButton, CONFIRM);
            setButtonListener(cancelButton, CANCEL);

            this.confirmDialogActors = new Actor[]{
                    blackNastyImage,
                    dialogBoxImage,
                    confirmButton,
                    cancelButton,
                    confirmLabel
            };

            this.addActorsToStage(confirmDialogActors);
        }
    }

    private void removeSelectedActorsFromStage(Actor... actors) {
        for (Actor actor : actors) {
            if (stage.getActors().contains(actor, true)) {
                stage.getActors().removeValue(actor, true);
            }
        }
        confirmDialogActors = null;
    }


}