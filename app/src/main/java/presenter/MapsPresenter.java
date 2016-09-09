package presenter;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.marcgdiez.clustering_android_gmaps.MapsActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import model.MarkerModel;

public class MapsPresenter {
    private MapsActivity view;

    public void setView(MapsActivity view) {
        this.view = view;
    }

    public void onMapReady() {
        List<MarkerModel> markerModelList = new ArrayList<>();
        for (int i = 0; i < 1000; i++){
            MarkerModel markerModel = new MarkerModel();
            LatLng latLng = new LatLng(getNewRandom(), getNewRandom());

            markerModel.setPosition(latLng);
            markerModelList.add(markerModel);
        }

        view.onDataLoaded(markerModelList);
    }

    private double getNewRandom() {
        Random r = new Random();
        int low = 20;
        int high = 60;
        return r.nextInt(high-low) + low;
    }

    public void pause() {

    }

    public void destroy() {

    }
}
