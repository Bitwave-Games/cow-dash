package com.bitwave.cowdash.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox.CheckBoxStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.bitwave.cowdash.utils.Assets;
import com.bitwave.cowdash.utils.AudioUtils;
import com.bitwave.cowdash.utils.BackButtonAlternatives;
import com.bitwave.cowdash.utils.ParticleHelper;
import com.bitwave.cowdash.utils.WorldType;
import com.bitwave.cowdash.utils.language.FontFinder;
import com.bitwave.cowdash.utils.language.LanguageBasedSkin;
import com.bitwave.cowdash.utils.persistance.CowPreferences;
import com.bitwave.cowdash.utils.ui.ScrollingImage;

public abstract class MenuBaseScreen extends BaseScreen {

    protected static float grassU;
    protected static float grassU2;
    protected static float mountainsU;
    protected static float mountainsU2;

    protected LanguageBasedSkin skin;
    protected TextureAtlas atlas;
    protected Stage stage;

    protected LabelStyle labelStyle_19_brownColor;
    protected LabelStyle labelStyle_16_whiteColor;

    protected ScrollingImage mountainsImage;
    protected ScrollingImage grassHillsImage;

    protected float worldWidth;
    protected float worldHeight;
    protected float centerX;

    public MenuBaseScreen(Game game, BackButtonAlternatives alternatives) {
        super(game);

        Assets assets = Assets.getInstance();
        this.stage = new Stage(new ExtendViewport(VIEWPORT_WIDTH, VIEWPORT_HEIGHT));

        this.worldWidth = stage.getViewport().getWorldWidth();
        this.worldHeight = stage.getViewport().getWorldHeight();
        this.centerX = worldWidth / 2;
        this.atlas = (TextureAtlas) assets.get("sprites/ui/menu.pack");

        this.skin = new LanguageBasedSkin(atlas);

        Color brownColor = new Color(101 / 255f, 79 / 255f, 33 / 255f, 1f);
        BitmapFont bitmapFont_19 = (BitmapFont) assets.get("fonts/cowfont_19.fnt");
        BitmapFont bitmapFont_16 = FontFinder.getFont("cowfont", 16);

        this.labelStyle_19_brownColor = new LabelStyle(bitmapFont_19, brownColor);
        this.labelStyle_16_whiteColor = new LabelStyle(bitmapFont_16, Color.WHITE);
        this.backButtonProcessor = getCustomizedInput(alternatives);

        WorldType worldType = WorldType.getValue(CowPreferences.getInstance().getCurrentWorld());
        this.mountainsImage = new ScrollingImage(worldType.getPathToBackgroundPNG(), 0.03f, mountainsU, mountainsU2, worldWidth);
        this.grassHillsImage = new ScrollingImage(worldType.getPathToForegroundPng(), 0.042f, grassU, grassU2, worldWidth);
        this.mountainsImage.setPosition(0, worldHeight - mountainsImage.getPrefHeight());
        this.grassHillsImage.setPosition(0, 0);

        if (!(this instanceof SplashScreen) && !(this instanceof MyCowScreen)) {
            this.addActorsToStage(mountainsImage, grassHillsImage);
        }

        InputMultiplexer multiplexer = new InputMultiplexer(stage, backButtonProcessor);
        Gdx.input.setInputProcessor(multiplexer);
    }

    @Override
    public void render(float delta) {
        camera.update();
        stage.draw();

        batch.setProjectionMatrix(stage.getCamera().combined);
        batch.begin();
        ParticleHelper.getInstance().render(batch, true);
        batch.end();

        tick(delta);
    }

    public abstract void tick(float deltaTime);

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        stage.getViewport().update(width, height, true);
        if (mountainsImage != null) {
            mountainsImage.setWorldWidth(stage.getViewport().getWorldWidth());
            mountainsImage.setPosition(0, stage.getViewport().getWorldHeight() - mountainsImage.getPrefHeight());
        }
        if (grassHillsImage != null) {
            grassHillsImage.setWorldWidth(stage.getViewport().getWorldWidth());
            grassHillsImage.setPosition(0, 0);
        }
    }

    protected void animateButtonAndFadeOutScreen(Actor button, float buttonFlashDuration, float fadeInDuration) {
        AudioUtils.getInstance().playSoundFX("button");
        button.addAction(Actions.repeat(3, Actions.sequence(Actions.fadeOut(buttonFlashDuration), Actions.after(Actions.fadeIn(buttonFlashDuration)))));
        stage.addAction(Actions.sequence(Actions.delay(buttonFlashDuration * 6f), Actions.fadeOut(fadeInDuration)));
    }

    protected float getCorrectY(Button button, float desiredYPos) {
        return worldHeight - button.getPrefHeight() - desiredYPos;
    }

    protected float getCorrectY(TextField textField, float desiredYPos) {
        return worldHeight - textField.getPrefHeight() - desiredYPos;
    }

    protected float getCorrectY(Image image, float desiredYPos) {
        return worldHeight - image.getPrefHeight() - desiredYPos;
    }

    protected float getCorrectY(Label label, float desiredYPos) {
        return worldHeight - label.getPrefHeight() - desiredYPos;
    }

    protected Image getCustomizedImage(String drawable, float x, float y, boolean visible) {
        Image image = new Image(skin.getDrawable(drawable));
        if (x > -1 || y > -1) {
            image.setPosition(x, getCorrectY(image, y));
        }
        image.setVisible(visible);
        return image;
    }

    protected Button getCustomizedButton(String up, String down, float x, float y, boolean visible) {
        Button button = new Button(skin.getDrawable(up), skin.getDrawable(down));
        button.setPosition(x, getCorrectY(button, y));
        button.setVisible(visible);
        return button;
    }

    protected CheckBox getCustomizedCheckBox(String unChecked, String checked, boolean visible, boolean isChecked) {
        CheckBox checkBox = new CheckBox("", new CheckBoxStyle(skin.getDrawable(unChecked), skin.getDrawable(checked), new BitmapFont(), null));
        checkBox.setVisible(visible);
        checkBox.setChecked(isChecked);
        checkBox.setTouchable(visible ? Touchable.enabled : Touchable.disabled);
        return checkBox;
    }

    protected Label getCustomizedLabel(String text, LabelStyle labelStyle, float x, float y, boolean visible) {
        Label label = new Label(text, labelStyle);
        label.setPosition(x, y);
        label.setVisible(visible);
        return label;
    }

    protected void addActorsToStage(Actor... actors) {
        for (Actor actor : actors) {
            stage.addActor(actor);
        }
    }

    protected void disableActors() {
        for (Actor actor : stage.getActors()) {
            actor.setTouchable(Touchable.disabled);
        }
    }

    protected boolean finishedActing(Actor actor) {
        return actor.getActions().size <= 0;
    }

    protected void saveUValuesForParallax() {
        mountainsU = mountainsImage.getU();
        mountainsU2 = mountainsImage.getU2();
        grassU = grassHillsImage.getU();
        grassU2 = grassHillsImage.getU2();
    }

    @Override
    public void hide() {
        super.hide();
        dispose();
    }

    @Override
    public void dispose() {
        super.dispose();
        stage.dispose();
    }
}