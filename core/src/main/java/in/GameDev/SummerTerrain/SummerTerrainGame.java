package in.GameDev.SummerTerrain;


import com.badlogic.gdx.Game;
import in.GameDev.SummerTerrain.screens.GameScreen;

public class SummerTerrainGame extends Game {

    @Override
    public void create() {
        setScreen(new GameScreen());
    }
}
