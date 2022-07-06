package com.bitwave.cowdash.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox.CheckBoxStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.bitwave.cowdash.CowDash;
import com.bitwave.cowdash.objects.AICow;
import com.bitwave.cowdash.objects.item.Back;
import com.bitwave.cowdash.objects.item.Body;
import com.bitwave.cowdash.objects.item.Head;
import com.bitwave.cowdash.objects.item.Leg;
import com.bitwave.cowdash.objects.item.Mask;
import com.bitwave.cowdash.utils.Assets;
import com.bitwave.cowdash.utils.AudioUtils;
import com.bitwave.cowdash.utils.BackButtonAlternatives;
import com.bitwave.cowdash.utils.ParticleHelper;
import com.bitwave.cowdash.utils.TextureUtils;
import com.bitwave.cowdash.utils.WorldType;
import com.bitwave.cowdash.utils.language.Translator;
import com.bitwave.cowdash.utils.persistance.CowPreferences;
import com.bitwave.cowdash.utils.ui.CustomCheckBox;
import com.bitwave.cowdash.utils.ui.WrappedImage;


public class MyCowScreen extends MenuBaseScreen {

    protected final String CLOTHING_BACK = "costume_back";
    protected final String CLOTHING_HEAD = "costume_head";
    protected final String CLOTHING_BODY = "costume_body";
    protected final String CLOTHING_MASK = "costume_mask";
    protected final String CLOTHING_LEG = "costume_leg";

    private final byte BLANK = -1;
    private final byte MASK = 0;
    private final byte HEAD = 1;
    private final byte BODY = 2;
    private final byte LEG = 3;
    private final byte CAPE = 4;
    private final byte BACK = 5;
    private final Animation changeClothesAnimation;
    private final Image whiteCanvasImage;
    private final BitmapFont bitmapFont_32;
    private final Array<Button> bodyButtons = new Array<Button>();
    private final Array<Button> clothesButtons = new Array<Button>();
    private byte selectedClothingCategory;
    private boolean backButtonPressed;
    private Array<ScrollPane> scrollPanes;
    private AICow aiCow;
    private ScrollPane currentScrollPane;
    private boolean shouldChangeClothes;
    private float cloudStateTime;
    private TextureRegion currentCloudFrame;
    private float stopPositionForCow;
    private float waitTimeForBeforeLoading;
    private boolean timeToShowCow;
    private Image returnToMenuButtonImage;
    private Image wardrobeTopImage;
    private Image itemsBoardImage;
    private WrappedImage tiledMapImage;
    private CheckBox maskButton;
    private CheckBox headButton;
    private CheckBox bodyButton;
    private CheckBox legButton;
    private CheckBox backButton;
    private Button returnToMenuButton;
    private boolean costumesAlreadyLoaded;
    private Label loadingLabel;
    private Float aiCowYPos;
    private final int currentBody = 0;
    private final int currentCloth = 0;

    public MyCowScreen(Game game, boolean shouldPlayMusic) {
        super(game, BackButtonAlternatives.BACK_TO_MENU);
        this.whiteCanvasImage = new Image(skin.getDrawable("whiterect"));
        this.bitmapFont_32 = (BitmapFont) Assets.getInstance().get("fonts/cowfont_32.fnt");
        if (shouldPlayMusic) {
            AudioUtils.getInstance().resetVolume();
            AudioUtils.getInstance().stopTrack("boss");
            AudioUtils.getInstance().playMusic("menu");
            whiteCanvasImage.setBounds(0, 0, worldWidth, worldHeight);
            whiteCanvasImage.addAction(Actions.sequence(Actions.fadeOut(1f), Actions.run(new Runnable() {
                @Override
                public void run() {
                    whiteCanvasImage.setVisible(false);
                }
            })));

        }
        this.changeClothesAnimation = new Animation(.1f, TextureUtils.singleSplit((Texture) Assets.getInstance().get("sprites/objects/cloud_strip4.png"), 32, 32, false));
        this.currentCloudFrame = (TextureRegion) changeClothesAnimation.getKeyFrame(0);

        this.grassHillsImage.setShouldMove(false);
        this.mountainsImage.setShouldMove(false);
        this.setStage();
    }

    @Override
    public void render(float delta) {
        camera.update();

        SpriteBatch stageBatch = (SpriteBatch) stage.getBatch();
        stageBatch.setProjectionMatrix(stage.getCamera().combined);
        stageBatch.begin();
        mountainsImage.draw(stageBatch, 1);
        grassHillsImage.draw(stageBatch, 1);
        stageBatch.end();

        stage.draw();

        batch.setProjectionMatrix(stage.getCamera().combined);
        batch.begin();
        if (shouldChangeClothes) {
            batch.draw(currentCloudFrame, centerX - 28, aiCow.getY() - 44, 64, 64);
        }
        ParticleHelper.getInstance().render(batch, true);
        batch.end();

        tick(delta);
    }

    @Override
    public void tick(float deltaTime) {
        camera.update();
        stage.act(deltaTime);
        mountainsImage.act(deltaTime);
        grassHillsImage.act(deltaTime);
        ParticleHelper.getInstance().tick(deltaTime, true);

        if (aiCow.getX() >= stopPositionForCow && !backButtonPressed) {
            aiCow.setShouldMove(false);
            if (waitTimeForBeforeLoading < .2f) {
                waitTimeForBeforeLoading += deltaTime;
            } else {
                initScrollPanes();
                if (loadingLabel.isVisible()) {
                    loadingLabel.setVisible(false);
                }
            }
        } else {
            if (timeToShowCow) {
                aiCow.setShouldMove(true);
            }
            if (aiCow.getX() > worldWidth / 1.14f) {
                animateStage(false);
            }
        }

        if (shouldChangeClothes) {
            cloudStateTime += deltaTime;
            currentCloudFrame = (TextureRegion) changeClothesAnimation.getKeyFrame(cloudStateTime, false);
            if (changeClothesAnimation.isAnimationFinished(cloudStateTime)) {
                shouldChangeClothes = false;
            }
        }

    }

    @Override
    public void resume() {
        super.resume();
        this.currentScrollPane = getMostLeftUnlockedCategory(maskButton, headButton, bodyButton, legButton, backButton);
        this.aiCow.setY(aiCowYPos);
    }

    private void setStage() {
        CowPreferences cowPreferences = CowPreferences.getInstance();
        wardrobeTopImage = new Image(skin.getDrawable("my_cow_bg_top"));
        returnToMenuButtonImage = new Image(skin.getDrawable("level_select_back_bg"));

        wardrobeTopImage.setPosition(centerX - wardrobeTopImage.getPrefWidth() / 2, getCorrectY(wardrobeTopImage, -worldHeight));
        returnToMenuButtonImage.setPosition(0, -returnToMenuButtonImage.getPrefHeight());

        BitmapFont defaultFont = new BitmapFont();
        maskButton = new CheckBox("", new CheckBoxStyle(skin.getDrawable("item_selector_mask_normal"), skin.getDrawable("item_selector_mask"), defaultFont, null));
        headButton = new CheckBox("", new CheckBoxStyle(skin.getDrawable("item_selector_hat_normal"), skin.getDrawable("item_selector_hat"), defaultFont, null));
        bodyButton = new CheckBox("", new CheckBoxStyle(skin.getDrawable("item_selector_shirt_normal"), skin.getDrawable("item_selector_shirt"), defaultFont, null));
        legButton = new CheckBox("", new CheckBoxStyle(skin.getDrawable("item_selector_pants_normal"), skin.getDrawable("item_selector_pants"), defaultFont, null));
        backButton = new CheckBox("", new CheckBoxStyle(skin.getDrawable("item_selector_back_normal"), skin.getDrawable("item_selector_back"), defaultFont, null));

        maskButton.setName(CLOTHING_MASK);
        headButton.setName(CLOTHING_HEAD);
        legButton.setName(CLOTHING_LEG);
        bodyButton.setName(CLOTHING_BODY);
        backButton.setName(CLOTHING_BACK);


        new ButtonGroup(maskButton, headButton, bodyButton, legButton, backButton).setMinCheckCount(1);

        maskButton.setVisible(cowPreferences.isItemTypeUnlocked(CLOTHING_MASK));
        headButton.setVisible(cowPreferences.isItemTypeUnlocked(CLOTHING_HEAD));
        bodyButton.setVisible(cowPreferences.isItemTypeUnlocked(CLOTHING_BODY));
        legButton.setVisible(cowPreferences.isItemTypeUnlocked(CLOTHING_LEG));
        backButton.setVisible(cowPreferences.isItemTypeUnlocked(CLOTHING_BACK));

        if (maskButton.isVisible()) {
            bodyButtons.add(maskButton);
        }
        if (headButton.isVisible()) {
            bodyButtons.add(headButton);
        }
        if (legButton.isVisible()) {
            bodyButtons.add(legButton);
        }
        if (bodyButton.isVisible()) {
            bodyButtons.add(bodyButton);
        }
        if (backButton.isVisible()) {
            bodyButtons.add(backButton);
        }
        maskButton.setPosition(wardrobeTopImage.getX(), getCorrectY(maskButton, 32 - worldHeight));
        headButton.setPosition(wardrobeTopImage.getX() + 64, getCorrectY(headButton, 32 - worldHeight));
        bodyButton.setPosition(wardrobeTopImage.getX() + 224, getCorrectY(bodyButton, 32 - worldHeight));
        legButton.setPosition(wardrobeTopImage.getX() + 160, getCorrectY(legButton, 32 - worldHeight));
        backButton.setPosition(wardrobeTopImage.getX() + 320, getCorrectY(backButton, 32 - worldHeight));


        returnToMenuButton = new Button(skin.getDrawable("level_select_back_normal"), skin.getDrawable("level_select_back"));
        returnToMenuButton.setPosition(returnToMenuButtonImage.getX() - 18, 5 - returnToMenuButtonImage.getPrefHeight());


        setListener(returnToMenuButton, BACK);
        setListener(backButton, CAPE);
        setListener(bodyButton, BODY);
        setListener(headButton, HEAD);
        setListener(legButton, LEG);
        setListener(maskButton, MASK);

        itemsBoardImage = new Image(skin.getDrawable("my_cow_cow_select_panel"));
        itemsBoardImage.setPosition(centerX - itemsBoardImage.getPrefWidth() / 2, getCorrectY(itemsBoardImage, -worldHeight));

        this.stopPositionForCow = centerX;
        this.aiCowYPos = 100f;
        this.aiCow = new AICow(-256, aiCowYPos, 64, 64, true);

        this.loadingLabel = new Label(Translator.getInstance().t("wardrobe"), labelStyle_19_brownColor);
        this.loadingLabel.setPosition(centerX - loadingLabel.getPrefWidth() / 2, worldHeight - (loadingLabel.getPrefHeight() * 2.5f));
        this.loadingLabel.setVisible(false);

        WorldType worldType = WorldType.getValue(cowPreferences.getCurrentWorld());
        switch (worldType) {
            case BEACH:
                this.tiledMapImage = new WrappedImage((Texture) Assets.getInstance().get("sprites/backgrounds/wardrobe-tile-beach.png"));
                break;
            default:
                this.tiledMapImage = new WrappedImage((Texture) Assets.getInstance().get("sprites/backgrounds/wardrobe-tile.png"));
                break;
        }
        this.tiledMapImage.setPosition(0, -70 - worldHeight);

        addActorsToStage(tiledMapImage, aiCow, wardrobeTopImage, returnToMenuButtonImage, itemsBoardImage, maskButton, headButton, bodyButton, legButton,
                backButton, loadingLabel, whiteCanvasImage, returnToMenuButton);


        animateStage(true);
    }

    private void animateStage(final boolean down) {
        float delay = down ? .66f : 0.0f;
        final float duration = down ? .8f : .66f;
        tiledMapImage.addAction(Actions.sequence(Actions.delay(down ? 0.0f : .66f), Actions.moveTo(tiledMapImage.getX(), down ? -58 : -70 - worldHeight, duration), Actions.run(new Runnable() {
            @Override
            public void run() {
                if (!down) {
                    game.setScreen(new MainMenuScreen(game, false));
                }
            }
        })));
        wardrobeTopImage.addAction(Actions.sequence(Actions.delay(delay), Actions.moveTo(wardrobeTopImage.getX(), getCorrectY(wardrobeTopImage, down ? 0 : -worldHeight), duration)));
        maskButton.addAction(Actions.sequence(Actions.delay(delay), Actions.moveTo(maskButton.getX(), getCorrectY(maskButton, down ? 32 : 32 - worldHeight), duration)));
        headButton.addAction(Actions.sequence(Actions.delay(delay), Actions.moveTo(headButton.getX(), getCorrectY(headButton, down ? 32 : 32 - worldHeight), duration)));
        bodyButton.addAction(Actions.sequence(Actions.delay(delay), Actions.moveTo(bodyButton.getX(), getCorrectY(bodyButton, down ? 32 : 32 - worldHeight), duration)));
        legButton.addAction(Actions.sequence(Actions.delay(delay), Actions.moveTo(legButton.getX(), getCorrectY(legButton, down ? 32 : 32 - worldHeight), duration)));
        backButton.addAction(Actions.sequence(Actions.delay(delay), Actions.moveTo(backButton.getX(), getCorrectY(backButton, down ? 32 : 32 - worldHeight), duration)));
        itemsBoardImage.addAction(Actions.sequence(Actions.delay(delay), Actions.moveTo(itemsBoardImage.getX(), getCorrectY(itemsBoardImage, down ? 0 : -worldHeight), duration)));

        if (!down) {
            stage.getActors().removeValue(currentScrollPane, true);
        }

        returnToMenuButtonImage.addAction(Actions.sequence(Actions.delay(delay), Actions.moveTo(returnToMenuButtonImage.getX(), down ? 0 : -returnToMenuButtonImage.getPrefHeight(), duration)));
        returnToMenuButton.addAction(Actions.sequence(Actions.delay(delay), Actions.moveTo(returnToMenuButton.getX(), down ? 5 : 5 - returnToMenuButtonImage.getPrefHeight(), duration)));

        stage.addAction(Actions.delay(duration / 2f, Actions.run(new Runnable() {

            @Override
            public void run() {
                if (down) {
                    loadingLabel.setVisible(true);
                    Table table = new Table();
                    table.setBackground(itemsBoardImage.getDrawable());
                    timeToShowCow = true;
                    if (currentScrollPane != null) {
                        stage.addActor(currentScrollPane);
                    }
                }
            }
        })));
    }

    private ScrollPane getMostLeftUnlockedCategory(Button... buttons) {
        for (byte i = 0; i < buttons.length; i++) {
            if (buttons[i].isVisible()) {
                String name = buttons[i].getName();
                buttons[i].setChecked(true);
                if (name.equalsIgnoreCase(CLOTHING_MASK)) {
                    selectedClothingCategory = MASK;
                    return scrollPanes.get(MASK);
                } else if (name.equalsIgnoreCase(CLOTHING_BODY)) {
                    selectedClothingCategory = BODY;
                    return scrollPanes.get(BODY);
                } else if (name.equalsIgnoreCase(CLOTHING_LEG)) {
                    selectedClothingCategory = LEG;
                    return scrollPanes.get(LEG);
                } else if (name.equalsIgnoreCase(CLOTHING_BACK)) {
                    selectedClothingCategory = CAPE;
                    return scrollPanes.get(CAPE);
                } else if (name.equalsIgnoreCase(CLOTHING_HEAD)) {
                    selectedClothingCategory = HEAD;
                    return scrollPanes.get(HEAD);
                }
            }
        }
        return null;
    }

    private void initScrollPanes() {
        if (!costumesAlreadyLoaded) {
            loadingLabel.setVisible(true);
            this.scrollPanes = new Array<ScrollPane>();
            scrollPanes.add(getPopulatedScrollPane(MASK, Mask.AMOUNT));
            scrollPanes.add(getPopulatedScrollPane(HEAD, Head.AMOUNT));
            scrollPanes.add(getPopulatedScrollPane(BODY, Body.AMOUNT));
            scrollPanes.add(getPopulatedScrollPane(LEG, Leg.AMOUNT));
            scrollPanes.add(getPopulatedScrollPane(CAPE, Back.AMOUNT));
            currentScrollPane = getMostLeftUnlockedCategory(maskButton, headButton, bodyButton, legButton, backButton);
            if (currentScrollPane != null) {
                loadingLabel.setVisible(false);
                stage.addActor(currentScrollPane);
            }
            costumesAlreadyLoaded = true;
        }
    }

    private void setListener(Actor actor, final byte ACTION) {
        actor.addListener(new InputListener() {

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
                    if (selectedClothingCategory != ACTION) {
                        ParticleHelper.getInstance().addWoodenEffect(event.getStageX(), event.getStageY(), true);
                        AudioUtils.getInstance().playSoundFX("button");
                        switch (ACTION) {
                            case HEAD:
                                selectedClothingCategory = HEAD;
                                replaceScrollerInStage(HEAD);
                                break;
                            case MASK:
                                selectedClothingCategory = MASK;
                                replaceScrollerInStage(MASK);
                                break;
                            case BODY:
                                selectedClothingCategory = BODY;
                                replaceScrollerInStage(BODY);
                                break;
                            case CAPE:
                                selectedClothingCategory = CAPE;
                                replaceScrollerInStage(CAPE);
                                break;
                            case LEG:
                                selectedClothingCategory = LEG;
                                replaceScrollerInStage(LEG);
                                break;
                            case BACK:
                                loadingLabel.setVisible(false);
                                saveUValuesForParallax();
                                backButtonPressed = true;
                                break;
                            case BLANK:
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
        });
    }

    private void replaceScrollerInStage(final byte type) {
        currentScrollPane.remove();
        currentScrollPane = scrollPanes.get(type);
        stage.addActor(currentScrollPane);
    }

    private ScrollPane getPopulatedScrollPane(final byte type, byte amountOfButtons) {
        switch (type) {
            case CAPE:
                return getScrollPane(amountOfButtons, CLOTHING_BACK, type);
            case LEG:
                return getScrollPane(amountOfButtons, CLOTHING_LEG, type);
            case HEAD:
                return getScrollPane(amountOfButtons, CLOTHING_HEAD, type);
            case BODY:
                return getScrollPane(amountOfButtons, CLOTHING_BODY, type);
            case MASK:
                return getScrollPane(amountOfButtons, CLOTHING_MASK, type);
            default:
                return getScrollPane(amountOfButtons, null, type);
        }
    }

    private ScrollPane getScrollPane(final byte amount, final String type, final byte cat) {

        Table table = new Table();
        final CowPreferences cowPreferences = CowPreferences.getInstance();
        if (type != null) {
            Array<Button> buttons = new Array<Button>();
            boolean allCostumesUnlocked = CowDash.ALL_COSTUMES_UNLOCKED;
            for (byte i = 0; i < amount; i++) {
                if (cowPreferences.isItemUnlocked(type, i) || i == 0 || allCostumesUnlocked) {
                    CustomCheckBox checkBox = new CustomCheckBox(skin, type, i);
                    checkBox.setName("" + i);
                    if (cowPreferences.getSavedClothing(type) == i) {
                        checkBox.setChecked(true);
                    }
                    buttons.add(checkBox);
                    table.add(checkBox).center().minSize(64, 64);
                }
            }

            Button[] lousyButtons = new Button[buttons.size];
            for (int i = 0; i < lousyButtons.length; i++) {
                lousyButtons[i] = buttons.get(i);
            }

            new ButtonGroup(lousyButtons).setMinCheckCount(1);

            for (final Button pressedButton : buttons) {
                pressedButton.addListener(new InputListener() {


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
                            if (pressedButton.isChecked()) {
                                byte itemIndex = Byte.parseByte(pressedButton.getName());
                                byte currentClothingIndex = CowPreferences.getInstance().getSavedClothing(type);

                                if (currentClothingIndex != itemIndex) {
                                    ParticleHelper.getInstance().addChangeClothesEffect(event.getStageX(), getCorrectY(pressedButton, pressedButton.getY() - (pressedButton.getPrefHeight() / 2)), false);

                                    AudioUtils.getInstance().playSoundFX("button");
                                    cloudStateTime = 0;
                                    shouldChangeClothes = true;

                                    cowPreferences.saveClothing(type, itemIndex);
                                    aiCow.changeClothing(type, itemIndex);
                                }
                            }
                        }
                    }
                });
            }
        }

        ScrollPane scrollPane = new ScrollPane(table);
        scrollPane.setBounds(itemsBoardImage.getX(), worldHeight - 64, itemsBoardImage.getWidth() - 20, table.getPrefHeight());
        return scrollPane;
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
