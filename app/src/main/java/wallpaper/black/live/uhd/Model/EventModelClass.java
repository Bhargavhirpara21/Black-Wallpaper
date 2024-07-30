package wallpaper.black.live.uhd.Model;

import java.io.Serializable;
import java.util.List;

public class EventModelClass implements IModel, Serializable {

    private List<WallpaperModel> wallpaper = null;

    int position;

    int fragmentType;

    public List<WallpaperModel> getWallpaper() {
        return wallpaper;
    }

    public void setWallpaper(List<WallpaperModel> wallpaper) {
        this.wallpaper = wallpaper;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getFragmentType() {
        return fragmentType;
    }

    public void setFragmentType(int fragmentType) {
        this.fragmentType = fragmentType;
    }
}
