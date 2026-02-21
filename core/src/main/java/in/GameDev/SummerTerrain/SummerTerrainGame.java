package in.GameDev.SummerTerrain;

import com.badlogic.gdx.Game;
import in.GameDev.SummerTerrain.screens.MainMenuScreen;

public class SummerTerrainGame extends Game {

    @Override
    public void create() {
        setScreen(new MainMenuScreen(this));
    }
}
