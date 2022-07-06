package com.bitwave.cowdash.utils;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.TmxMapLoader.Parameters;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Logger;
import com.bitwave.cowdash.CowDash;

public class Assets implements Disposable {

    private static Assets assets;
    private final AssetManager manager;

    private Assets() {
        manager = new AssetManager();
        if (CowDash.DEVELOPER_MODE) {
            loadInGameAssets();
            loadPixmaps();
            loadAudio();
            loadMenuAssets();
            Texture.setAssetManager(manager);
            manager.finishLoading();
        }
    }

    public static Assets getInstance() {
        if (assets == null) {
            assets = new Assets();
        }
        return assets;
    }

    public Object get(String fileName) {
        return manager.get(fileName);
    }

    public Sprite getSprite(String path) {
        Sprite sprite = new Sprite((Texture) get(path));
        return sprite;
    }

    public Sprite getSprite(String path, boolean flipX, boolean flipY) {
        Sprite sprite = new Sprite((Texture) get(path));
        sprite.flip(flipX, flipY);
        return sprite;
    }

    @Override
    public void dispose() {
        manager.dispose();
    }

    public void clear() {
        manager.clear();
    }

    public void loadMenuAssets() {
        System.gc();
        manager.load("sprites/ui/menu.pack", TextureAtlas.class);

        manager.load("fonts/cowfont_32.fnt", BitmapFont.class);
        manager.load("fonts/cowfont_19.fnt", BitmapFont.class);
        manager.load("fonts/cowfont_16.fnt", BitmapFont.class);
        manager.load("fonts/cowfont_12.fnt", BitmapFont.class);
        manager.load("fonts/cowfont_10.fnt", BitmapFont.class);

        manager.load("sprites/objects/scenery/treetops_birch_32.png", Texture.class);
        manager.load("sprites/ui/retroidinteractive-logo.png", Texture.class);
        manager.load("sprites/objects/cloud_strip4.png", Texture.class);
        manager.load("sprites/in/sunset_pink_strip2.png", Texture.class);
        manager.load("sprites/ui/wunderling_promo.png", Texture.class);

        manager.load("sprites/backgrounds/mediumcloud.png", Texture.class);
        manager.load("sprites/backgrounds/smallcloud.png", Texture.class);
        manager.load("sprites/backgrounds/grass-world/grass_fg.png", Texture.class);
        manager.load("sprites/backgrounds/grass-world/grass_bg.png", Texture.class);
        manager.load("sprites/backgrounds/grass-world-premium/grass_fg.png", Texture.class);
        manager.load("sprites/backgrounds/grass-world-premium/grass_bg.png", Texture.class);
        manager.load("sprites/backgrounds/beach-world/beach_bg.png", Texture.class);
        manager.load("sprites/backgrounds/beach-world/beach_fg.png", Texture.class);
        manager.load("sprites/backgrounds/wardrobe-tile.png", Texture.class);
        manager.load("sprites/backgrounds/wardrobe-tile-beach.png", Texture.class);

        manager.load("sprites/backgrounds/beach_cloud.png", Texture.class);
        manager.load("sprites/backgrounds/keyart.png", Texture.class);
        manager.load("sprites/backgrounds/sunset_cloud.png", Texture.class);

        manager.load("sprites/in/cow_dash_logo.png", Texture.class);
        manager.load("sprites/in/level_select_Screen_1.png", Texture.class);
        manager.load("sprites/in/world_select_screen_beach_premium.png", Texture.class);
        manager.load("sprites/in/world_select_scren_long.png", Texture.class);
        manager.load("sprites/in/world_select_screen_beach.png", Texture.class);

        for (int i = 0; i < 299; i++) {
            if (i < 10) {
                manager.load("splash/bitwave-splash-000" + i + ".png", Texture.class);
                continue;
            }
            if (i < 100) {
                manager.load("splash/bitwave-splash-00" + i + ".png", Texture.class);
                continue;
            }
            manager.load("splash/bitwave-splash-0" + i + ".png", Texture.class);
            continue;
        }

        Texture.setAssetManager(manager);
    }

    public void unloadMenuAssets() {
        manager.unload("sprites/ui/menu.pack");

        manager.unload("fonts/cowfont_32.fnt");
        manager.unload("fonts/cowfont_19.fnt");
        manager.unload("fonts/cowfont_16.fnt");
        manager.unload("fonts/cowfont_12.fnt");
        manager.unload("fonts/cowfont_10.fnt");

        manager.unload("sprites/objects/scenery/treetops_birch_32.png");
        manager.unload("sprites/ui/retroidinteractive-logo.png");
        manager.unload("sprites/objects/cloud_strip4.png");
        manager.unload("sprites/in/sunset_pink_strip2.png");

        manager.unload("sprites/backgrounds/mediumcloud.png");
        manager.unload("sprites/backgrounds/smallcloud.png");
        manager.unload("sprites/backgrounds/grass-world/grass_fg.png");
        manager.unload("sprites/backgrounds/grass-world/grass_bg.png");
        manager.unload("sprites/backgrounds/grass-world-premium/grass_fg.png");
        manager.unload("sprites/backgrounds/grass-world-premium/grass_bg.png");
        manager.unload("sprites/backgrounds/beach-world/beach_bg.png");
        manager.unload("sprites/backgrounds/beach-world/beach_fg.png");
        manager.unload("sprites/backgrounds/wardrobe-tile.png");
        manager.unload("sprites/backgrounds/wardrobe-tile-beach.png");


        manager.unload("sprites/backgrounds/beach_cloud.png");
        manager.unload("sprites/backgrounds/sunset_cloud.png");

        manager.unload("sprites/in/cow_dash_logo.png");
        manager.unload("sprites/in/level_select_Screen_1.png");
        manager.unload("sprites/in/world_select_screen_beach_premium.png");
        manager.unload("sprites/in/world_select_scren_long.png");
        manager.unload("sprites/in/world_select_screen_beach.png");

    }

    public void loadInGameAssets() {
        System.gc();
        manager.load("sprites/ui/dialogs.pack", TextureAtlas.class);
        manager.load("sprites/in/whitetexture.png", Texture.class);

        manager.load("fonts/cowfont_32.fnt", BitmapFont.class);
        manager.load("fonts/cowfont_19.fnt", BitmapFont.class);
        manager.load("fonts/cowfont_16.fnt", BitmapFont.class);
        manager.load("fonts/cowfont_12.fnt", BitmapFont.class);
        manager.load("fonts/cowfont_10.fnt", BitmapFont.class);
        manager.load("fonts/vag-10.fnt", BitmapFont.class);

        manager.load("sprites/in/tutorial_jump_long_strip4" + ".png", Texture.class);
        manager.load("sprites/in/tutorial_1" + ".png", Texture.class);
        manager.load("sprites/in/tutorial_pig_colored" + ".png", Texture.class);
        manager.load("sprites/objects/cloud_strip4.png", Texture.class);
        manager.load("sprites/in/tutorial_walljump_strip5.png", Texture.class);

        manager.load("sprites/objects/teleporter.png", Texture.class);
        manager.load("sprites/objects/birdsheet.png", Texture.class);
        manager.load("sprites/objects/golden_veggie_16x16.png", Texture.class);
        manager.load("sprites/objects/Pickup_sheet_2.png", Texture.class);
        manager.load("sprites/objects/beach_veggies_16.png", Texture.class);
        manager.load("sprites/objects/Cactus_ball_black2.png", Texture.class);
        manager.load("sprites/objects/health_bar_2.png", Texture.class);
        manager.load("sprites/objects/win_orb_spritesheet_bw_strip9.png", Texture.class);
        manager.load("sprites/objects/portal_gradient.png", Texture.class);
        manager.load("sprites/backgrounds/blank.png", Texture.class);
        manager.load("sprites/objects/wall_flipper.png", Texture.class);

        manager.load("sprites/objects/scenery/bush_sheet_32.png", Texture.class);
        manager.load("sprites/objects/scenery/treetops_big_96.png", Texture.class);
        manager.load("sprites/objects/scenery/treetops_birch_32.png", Texture.class);
        manager.load("sprites/objects/scenery/treetops_maple_32.png", Texture.class);
        manager.load("sprites/objects/scenery/flag_strip3.png", Texture.class);
        manager.load("sprites/objects/scenery/palm_treetop_32x32_strip4.png", Texture.class);
        manager.load("sprites/objects/scenery/windy_weed_strip4.png", Texture.class);

        manager.load("sprites/backgrounds/mediumcloud.png", Texture.class);
        manager.load("sprites/backgrounds/smallcloud.png", Texture.class);
        manager.load("sprites/backgrounds/grass-world/grass_fg.png", Texture.class);
        manager.load("sprites/backgrounds/grass-world/grass_bg.png", Texture.class);
        manager.load("sprites/backgrounds/grass-world-premium/grass_fg.png", Texture.class);
        manager.load("sprites/backgrounds/grass-world-premium/grass_bg.png", Texture.class);
        manager.load("sprites/backgrounds/beach-world/beach_bg.png", Texture.class);
        manager.load("sprites/backgrounds/beach-world/beach_fg.png", Texture.class);
        manager.load("sprites/backgrounds/beach_cloud.png", Texture.class);
        manager.load("sprites/backgrounds/sunset_cloud.png", Texture.class);

        manager.load("sprites/objects/enemies/enemy_4_blob_death_sprite_sheet_strip7.png", Texture.class);
        manager.load("sprites/objects/enemies/beach_pig_all_strip11.png", Texture.class);
        manager.load("sprites/objects/enemies/grass_pig_all_strip11.png", Texture.class);
        manager.load("sprites/objects/enemies/walking_enemy_spritesheet.png", Texture.class);
        manager.load("sprites/objects/enemies/spring_enemy_pink_upped_strip11.png", Texture.class);
        manager.load("sprites/objects/enemies/LASTTTT.png", Texture.class);
        manager.load("sprites/objects/enemies/spring_death_spritesheet_strip7.png", Texture.class);
        manager.load("sprites/objects/enemies/cactus_blink_strip2.png", Texture.class);
        manager.load("sprites/objects/enemies/blockguy_death_sepia_strip11.png", Texture.class);
        manager.load("sprites/objects/enemies/blockguy_sepia_strip13.png", Texture.class);

        manager.load("sprites/objects/doors/block_door_blue.png", Texture.class);
        manager.load("sprites/objects/doors/block_door_red.png", Texture.class);
        manager.load("sprites/objects/doors/block_door_yellow.png", Texture.class);
        manager.load("sprites/objects/doors/DOWNonewaydoor_spritesheet.png", Texture.class);
        manager.load("sprites/objects/doors/LEFTonewaydoor_spritesheet.png", Texture.class);
        manager.load("sprites/objects/doors/RIGHTonewaydoor_spritesheet.png", Texture.class);
        manager.load("sprites/objects/keys/key_blue.png", Texture.class);
        manager.load("sprites/objects/keys/key_red.png", Texture.class);
        manager.load("sprites/objects/keys/key_yellow.png", Texture.class);
        manager.load("sprites/objects/chest_sprite_sheet.png", Texture.class);

        Texture.setAssetManager(manager);
    }

    public void loadTileMap(WorldType worldType, byte index) {
        manager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));

        Parameters p = new Parameters();
        p.textureMagFilter = TextureFilter.Nearest;
        p.textureMinFilter = TextureFilter.Nearest;

        String path = "levels/" + worldType.getDisplayName().toLowerCase() + "/level-";
        manager.load(path + index + ".tmx", TiledMap.class, p);
        manager.finishLoading();
    }

    public void unloadTileMap(WorldType worldType, byte index) {
        try {
            String path = "levels/" + worldType.getDisplayName().toLowerCase() + "/level-";
            manager.unload(path + index + ".tmx");
        } catch (GdxRuntimeException gre) {
            Logger l = new Logger("IOError", Logger.ERROR);
            l.error(gre.getLocalizedMessage());
        }
    }

    public void unloadInGameAssets() {
        manager.unload("sprites/ui/dialogs.pack");
        manager.unload("fonts/cowfont_32.fnt");
        manager.unload("fonts/cowfont_19.fnt");
        manager.unload("fonts/cowfont_16.fnt");
        manager.unload("fonts/cowfont_12.fnt");
        manager.unload("fonts/cowfont_10.fnt");
        manager.unload("sprites/objects/teleporter.png");
        manager.unload("sprites/objects/birdsheet.png");
        manager.unload("sprites/objects/Pickup_sheet_2.png");
        manager.unload("sprites/objects/Cactus_ball_black2.png");
        manager.unload("sprites/objects/health_bar_2.png");
        manager.unload("sprites/objects/win_orb_spritesheet_bw_strip9.png");
        manager.unload("sprites/objects/portal_gradient.png");
        manager.unload("sprites/backgrounds/blank.png");
        manager.unload("sprites/objects/beach_veggies_16.png");
        manager.unload("sprites/objects/wall_flipper.png");
        manager.unload("sprites/objects/scenery/bush_sheet_32.png");
        manager.unload("sprites/objects/scenery/treetops_big_96.png");
        manager.unload("sprites/objects/scenery/treetops_birch_32.png");
        manager.unload("sprites/objects/scenery/treetops_maple_32.png");
        manager.unload("sprites/objects/scenery/flag_strip3.png");
        manager.unload("sprites/objects/scenery/palm_treetop_32x32_strip4.png");
        manager.unload("sprites/objects/scenery/windy_weed_strip4.png");
        manager.unload("sprites/objects/enemies/enemy_4_blob_death_sprite_sheet_strip7.png");
        manager.unload("sprites/objects/enemies/beach_pig_all_strip11.png");
        manager.unload("sprites/objects/enemies/grass_pig_all_strip11.png");
        manager.unload("sprites/objects/enemies/walking_enemy_spritesheet.png");
        manager.unload("sprites/objects/enemies/spring_enemy_pink_upped_strip11.png");
        manager.unload("sprites/objects/enemies/LASTTTT.png");
        manager.unload("sprites/objects/enemies/spring_death_spritesheet_strip7.png");
        manager.unload("sprites/objects/enemies/cactus_blink_strip2.png");
        manager.unload("sprites/objects/enemies/blockguy_death_sepia_strip11.png");
        manager.unload("sprites/objects/enemies/blockguy_sepia_strip13.png");
        manager.unload("sprites/objects/doors/block_door_blue.png");
        manager.unload("sprites/objects/doors/block_door_red.png");
        manager.unload("sprites/objects/doors/block_door_yellow.png");
        manager.unload("sprites/objects/doors/DOWNonewaydoor_spritesheet.png");
        manager.unload("sprites/objects/doors/LEFTonewaydoor_spritesheet.png");
        manager.unload("sprites/objects/doors/RIGHTonewaydoor_spritesheet.png");
        manager.unload("sprites/objects/keys/key_blue.png");
        manager.unload("sprites/objects/keys/key_red.png");
        manager.unload("sprites/objects/keys/key_yellow.png");
        manager.unload("sprites/objects/chest_sprite_sheet.png");
        manager.unload("sprites/objects/golden_veggie_16x16.png");

        manager.unload("sprites/in/tutorial_jump_long_strip4" + ".png");
        manager.unload("sprites/in/tutorial_1" + ".png");
        manager.unload("sprites/in/tutorial_pig_colored" + ".png");
        manager.unload("sprites/objects/cloud_strip4.png");
        manager.unload("sprites/in/tutorial_walljump_strip5.png");

        manager.unload("sprites/backgrounds/mediumcloud.png");
        manager.unload("sprites/backgrounds/smallcloud.png");
        manager.unload("sprites/backgrounds/grass-world/grass_fg.png");
        manager.unload("sprites/backgrounds/grass-world/grass_bg.png");
        manager.unload("sprites/backgrounds/grass-world-premium/grass_fg.png");
        manager.unload("sprites/backgrounds/grass-world-premium/grass_bg.png");
        manager.unload("sprites/backgrounds/beach-world/beach_bg.png");
        manager.unload("sprites/backgrounds/beach-world/beach_fg.png");
        manager.unload("sprites/backgrounds/beach_cloud.png");
        manager.unload("sprites/backgrounds/sunset_cloud.png");
        manager.unload("sprites/in/whitetexture.png");
    }

    public void unloadPixmaps() {
        if (Gdx.app.getType() == ApplicationType.Desktop) {
            manager.unload("sprites/ui/disgusting_arrow.png");
        }
        manager.unload("sprites/objects/cow.png");
        manager.unload("sprites/items/costumes/none.png");
        manager.unload("sprites/items/costumes/mask/clown_nose.png");
        manager.unload("sprites/items/costumes/mask/eypatch.png");
        manager.unload("sprites/items/costumes/mask/monocular.png");
        manager.unload("sprites/items/costumes/mask/nosering.png");
        manager.unload("sprites/items/costumes/mask/paperbag.png");
        manager.unload("sprites/items/costumes/mask/sunglasses.png");
        manager.unload("sprites/items/costumes/mask/tikimask.png");
        manager.unload("sprites/items/costumes/mask/censored.png");
        manager.unload("sprites/items/costumes/mask/bane_mask.png");
        manager.unload("sprites/items/costumes/mask/christopherglasses.png");
        manager.unload("sprites/items/costumes/mask/ragnarmask.png");
        manager.unload("sprites/items/costumes/mask/tonymask.png");
        manager.unload("sprites/items/costumes/mask/nerdglasses.png");
        manager.unload("sprites/items/costumes/head/bandana.png");
        manager.unload("sprites/items/costumes/head/astrohelmet.png");
        manager.unload("sprites/items/costumes/head/bunnyears.png");
        manager.unload("sprites/items/costumes/head/clownwig.png");
        manager.unload("sprites/items/costumes/head/cowboyhat.png");
        manager.unload("sprites/items/costumes/head/helmet.png");
        manager.unload("sprites/items/costumes/head/kylehat.png");
        manager.unload("sprites/items/costumes/head/magichat.png");
        manager.unload("sprites/items/costumes/head/mohawk.png");
        manager.unload("sprites/items/costumes/head/newspaper.png");
        manager.unload("sprites/items/costumes/head/ninjaheadband.png");
        manager.unload("sprites/items/costumes/head/peachhat.png");
        manager.unload("sprites/items/costumes/head/pimphat.png");
        manager.unload("sprites/items/costumes/head/piratehat.png");
        manager.unload("sprites/items/costumes/head/radar.png");
        manager.unload("sprites/items/costumes/head/rasta_hat.png");
        manager.unload("sprites/items/costumes/head/santahat.png");
        manager.unload("sprites/items/costumes/head/stanhat.png");
        manager.unload("sprites/items/costumes/head/christopherhat.png");
        manager.unload("sprites/items/costumes/head/feather_hat.png");
        manager.unload("sprites/items/costumes/back/cape.png");
        manager.unload("sprites/items/costumes/back/angelwings.png");
        manager.unload("sprites/items/costumes/back/windup_spring.png");
        manager.unload("sprites/items/costumes/back/turtle_shell.png");
        manager.unload("sprites/items/costumes/back/swimring.png");
        manager.unload("sprites/items/costumes/back/saddle.png");
        manager.unload("sprites/items/costumes/back/propeller.png");
        manager.unload("sprites/items/costumes/back/hangglider.png");
        manager.unload("sprites/items/costumes/back/boombox.png");
        manager.unload("sprites/items/costumes/body/astrosuit.png");
        manager.unload("sprites/items/costumes/body/ninjaoutfit.png");
        manager.unload("sprites/items/costumes/body/banejacket.png");
        manager.unload("sprites/items/costumes/body/tanktop_colorized.png");
        manager.unload("sprites/items/costumes/body/windup_armor.png");
        manager.unload("sprites/items/costumes/legs/wooden_leg.png");
        manager.unload("sprites/items/costumes/legs/clownpants.png");
        manager.unload("sprites/items/costumes/legs/jeans.png");
        manager.unload("sprites/items/costumes/legs/windup_pants.png");

        manager.unload("sprites/items/costumes/mask/cucumber_eyes.png");
        manager.unload("sprites/items/costumes/mask/makeup.png");
        manager.unload("sprites/items/costumes/mask/nose.png");
        manager.unload("sprites/items/costumes/head/donnawig.png");
        manager.unload("sprites/items/costumes/head/elviswig.png");
        manager.unload("sprites/items/costumes/head/namhelmet.png");
        manager.unload("sprites/items/costumes/head/ragnarhat.png");
        manager.unload("sprites/items/costumes/head/ribbon.png");
        manager.unload("sprites/items/costumes/head/batmanmask.png");
        manager.unload("sprites/items/costumes/head/hasselhoff_wig.png");
        manager.unload("sprites/items/costumes/head/bighorns.png");
        manager.unload("sprites/items/costumes/head/sombrero.png");
        manager.unload("sprites/items/costumes/head/windup_helmet.png");
        manager.unload("sprites/items/costumes/head/windup_spring.png");
        manager.unload("sprites/items/costumes/body/balletdress.png");
        manager.unload("sprites/items/costumes/body/cyborg.png");
        manager.unload("sprites/items/costumes/mask/demoneyes2.png");
        manager.unload("sprites/items/costumes/body/black_vest.png");
        manager.unload("sprites/items/costumes/body/red_jeans.png");
        manager.unload("sprites/items/costumes/body/brown_jeans.png");
    }

    public void loadPixmaps() {
        if (Gdx.app.getType() == ApplicationType.Desktop) {
            manager.load("sprites/ui/disgusting_arrow.png", Pixmap.class);
        }

        manager.load("sprites/objects/cow.png", Pixmap.class);

        manager.load("sprites/items/costumes/none.png", Pixmap.class);

        manager.load("sprites/items/costumes/mask/clown_nose.png", Pixmap.class);
        manager.load("sprites/items/costumes/mask/eypatch.png", Pixmap.class);
        manager.load("sprites/items/costumes/mask/monocular.png", Pixmap.class);
        manager.load("sprites/items/costumes/mask/nosering.png", Pixmap.class);
        manager.load("sprites/items/costumes/mask/paperbag.png", Pixmap.class);
        manager.load("sprites/items/costumes/mask/sunglasses.png", Pixmap.class);
        manager.load("sprites/items/costumes/mask/tikimask.png", Pixmap.class);
        manager.load("sprites/items/costumes/mask/censored.png", Pixmap.class);
        manager.load("sprites/items/costumes/mask/bane_mask.png", Pixmap.class);
        manager.load("sprites/items/costumes/mask/christopherglasses.png", Pixmap.class);
        manager.load("sprites/items/costumes/mask/ragnarmask.png", Pixmap.class);
        manager.load("sprites/items/costumes/mask/tonymask.png", Pixmap.class);
        manager.load("sprites/items/costumes/mask/cucumber_eyes.png", Pixmap.class);
        manager.load("sprites/items/costumes/mask/makeup.png", Pixmap.class);
        manager.load("sprites/items/costumes/mask/nose.png", Pixmap.class);
        manager.load("sprites/items/costumes/mask/demoneyes2.png", Pixmap.class);
        manager.load("sprites/items/costumes/mask/nerdglasses.png", Pixmap.class);

        manager.load("sprites/items/costumes/head/bandana.png", Pixmap.class);
        manager.load("sprites/items/costumes/head/astrohelmet.png", Pixmap.class);
        manager.load("sprites/items/costumes/head/bunnyears.png", Pixmap.class);
        manager.load("sprites/items/costumes/head/clownwig.png", Pixmap.class);
        manager.load("sprites/items/costumes/head/cowboyhat.png", Pixmap.class);
        manager.load("sprites/items/costumes/head/helmet.png", Pixmap.class);
        manager.load("sprites/items/costumes/head/kylehat.png", Pixmap.class);
        manager.load("sprites/items/costumes/head/magichat.png", Pixmap.class);
        manager.load("sprites/items/costumes/head/mohawk.png", Pixmap.class);
        manager.load("sprites/items/costumes/head/newspaper.png", Pixmap.class);
        manager.load("sprites/items/costumes/head/ninjaheadband.png", Pixmap.class);
        manager.load("sprites/items/costumes/head/peachhat.png", Pixmap.class);
        manager.load("sprites/items/costumes/head/pimphat.png", Pixmap.class);
        manager.load("sprites/items/costumes/head/piratehat.png", Pixmap.class);
        manager.load("sprites/items/costumes/head/radar.png", Pixmap.class);
        manager.load("sprites/items/costumes/head/rasta_hat.png", Pixmap.class);
        manager.load("sprites/items/costumes/head/santahat.png", Pixmap.class);
        manager.load("sprites/items/costumes/head/stanhat.png", Pixmap.class);
        manager.load("sprites/items/costumes/head/christopherhat.png", Pixmap.class);
        manager.load("sprites/items/costumes/head/feather_hat.png", Pixmap.class);
        manager.load("sprites/items/costumes/head/batmanmask.png", Pixmap.class);
        manager.load("sprites/items/costumes/head/hasselhoff_wig.png", Pixmap.class);
        manager.load("sprites/items/costumes/head/bighorns.png", Pixmap.class);

        manager.load("sprites/items/costumes/head/donnawig.png", Pixmap.class);
        manager.load("sprites/items/costumes/head/elviswig.png", Pixmap.class);
        manager.load("sprites/items/costumes/head/namhelmet.png", Pixmap.class);
        manager.load("sprites/items/costumes/head/ragnarhat.png", Pixmap.class);
        manager.load("sprites/items/costumes/head/ribbon.png", Pixmap.class);
        manager.load("sprites/items/costumes/head/sombrero.png", Pixmap.class);
        manager.load("sprites/items/costumes/head/windup_helmet.png", Pixmap.class);

        manager.load("sprites/items/costumes/back/cape.png", Pixmap.class);
        manager.load("sprites/items/costumes/back/angelwings.png", Pixmap.class);
        manager.load("sprites/items/costumes/back/windup_spring.png", Pixmap.class);
        manager.load("sprites/items/costumes/back/turtle_shell.png", Pixmap.class);
        manager.load("sprites/items/costumes/back/swimring.png", Pixmap.class);
        manager.load("sprites/items/costumes/back/saddle.png", Pixmap.class);
        manager.load("sprites/items/costumes/back/propeller.png", Pixmap.class);
        manager.load("sprites/items/costumes/back/hangglider.png", Pixmap.class);
        manager.load("sprites/items/costumes/back/boombox.png", Pixmap.class);

        manager.load("sprites/items/costumes/body/astrosuit.png", Pixmap.class);
        manager.load("sprites/items/costumes/body/ninjaoutfit.png", Pixmap.class);
        manager.load("sprites/items/costumes/body/banejacket.png", Pixmap.class);
        manager.load("sprites/items/costumes/body/tanktop_colorized.png", Pixmap.class);
        manager.load("sprites/items/costumes/body/balletdress.png", Pixmap.class);
        manager.load("sprites/items/costumes/body/cyborg.png", Pixmap.class);
        manager.load("sprites/items/costumes/body/black_vest.png", Pixmap.class);
        manager.load("sprites/items/costumes/body/red_jeans.png", Pixmap.class);
        manager.load("sprites/items/costumes/body/brown_jeans.png", Pixmap.class);
        manager.load("sprites/items/costumes/body/windup_armor.png", Pixmap.class);

        manager.load("sprites/items/costumes/legs/wooden_leg.png", Pixmap.class);
        manager.load("sprites/items/costumes/legs/clownpants.png", Pixmap.class);
        manager.load("sprites/items/costumes/legs/jeans.png", Pixmap.class);
        manager.load("sprites/items/costumes/legs/windup_pants.png", Pixmap.class);
    }

    public void loadAudio() {
        manager.load("sound/music/bonush crash no moo.mp3", Music.class);
        manager.load("sound/music/intro.mp3", Music.class);
        manager.load("sound/effects/button.mp3", Sound.class);
        manager.load("sound/music/menu.mp3", Music.class);
        manager.load("sound/music/grass.mp3", Music.class);
        manager.load("sound/music/ghost.mp3", Music.class);
        manager.load("sound/music/boss.mp3", Music.class);
        manager.load("sound/music/beach.mp3", Music.class);
        manager.load("sound/effects/pickup.mp3", Sound.class);
        manager.load("sound/effects/go.mp3", Sound.class);
        manager.load("sound/effects/cowdie.mp3", Sound.class);
        manager.load("sound/effects/getkey.mp3", Sound.class);
        manager.load("sound/effects/opendoor.mp3", Sound.class);
        manager.load("sound/effects/EatMunch.mp3", Sound.class);
        manager.load("sound/effects/FlipTile.mp3", Sound.class);
        manager.load("sound/effects/FlipTileEnd.mp3", Sound.class);
        manager.load("sound/effects/WeaponBonus.mp3", Sound.class);
        manager.load("sound/effects/BonusCrystal.mp3", Sound.class);
        manager.load("sound/effects/pig_jump.mp3", Sound.class);
        manager.load("sound/effects/medal.mp3", Sound.class);
        manager.load("sound/effects/pig_high_jump.mp3", Sound.class);
        manager.load("sound/effects/cow-1.mp3", Sound.class);
        manager.load("sound/effects/whip.mp3", Sound.class);
        manager.load("sound/effects/walljump.mp3", Sound.class);
        manager.load("sound/effects/teleport.mp3", Sound.class);
    }

    public int getProgress() {
        return (int) (manager.getProgress() * 100);
    }

    public boolean isLoading() {
        return !manager.update();
    }

    public boolean isLoaded(String path) {
        return manager.isLoaded(path);
    }

    public void finishLoading() {
        manager.finishLoading();
        Texture.setAssetManager(manager);
    }

}