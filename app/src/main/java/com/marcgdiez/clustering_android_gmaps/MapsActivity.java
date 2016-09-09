/**
 * Copyright 2016 Marc González Díez
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marcgdiez.clustering_android_gmaps;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import java.util.List;

import model.MarkerModel;
import presenter.MapsPresenter;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private MapsPresenter mapsPresenter;
    private ClusterManager<MarkerModel> clusterManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        initFab();
        initPresenter();
        initMap();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapsPresenter.pause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapsPresenter.destroy();
    }

    private void initFab() {

    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void initPresenter() {
        mapsPresenter = new MapsPresenter();
        mapsPresenter.setView(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        initClustering();
        mapsPresenter.onMapReady();
    }

    private void initClustering() {
        clusterManager = new ClusterManager<>(this, mMap);
        clusterManager.setRenderer(new CustomClusterRenderer(this, mMap,
                clusterManager));

        mMap.setOnCameraChangeListener(clusterManager);
        mMap.setOnMarkerClickListener(clusterManager);
        mMap.setOnInfoWindowClickListener(clusterManager);
        mMap.setInfoWindowAdapter(clusterManager.getMarkerManager());
        mMap.setOnInfoWindowClickListener(clusterManager);

        clusterManager.setOnClusterClickListener(new ClusterManager
                .OnClusterClickListener<MarkerModel>() {
            @Override
            public boolean onClusterClick(Cluster<MarkerModel> cluster) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                cluster.getPosition(), (float) Math.floor(mMap
                                        .getCameraPosition().zoom + 1)), 300,
                        null);
                return true;
            }
        });
        clusterManager.setOnClusterInfoWindowClickListener(
                new ClusterManager.OnClusterInfoWindowClickListener<MarkerModel>() {
                    @Override
                    public void onClusterInfoWindowClick(Cluster<MarkerModel> cluster) {
                        //Workaround needed to avoid non-clickable clusters
                    }
                });
        clusterManager.setOnClusterItemInfoWindowClickListener(
                new ClusterManager.OnClusterItemInfoWindowClickListener<MarkerModel>() {
                    @Override
                    public void onClusterItemInfoWindowClick(MarkerModel
                                                                     marker) {
                        //Workaround needed to avoid non-clickable clusters
                    }
                });
        clusterManager.setOnClusterItemClickListener(new ClusterManager
                .OnClusterItemClickListener<MarkerModel>() {
            @Override
            public boolean onClusterItemClick(MarkerModel marker) {
                Toast.makeText(MapsActivity.this, "Position: " + marker.getPosition(), Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private class CustomClusterRenderer extends DefaultClusterRenderer<MarkerModel> {

        public CustomClusterRenderer(Context context, GoogleMap map, ClusterManager<MarkerModel> clusterManager) {
            super(context, map, clusterManager);
        }

        @Override
        protected void onClusterItemRendered(MarkerModel clusterItem, Marker marker) {
            super.onClusterItemRendered(clusterItem, marker);
        }

        @Override
        protected void onBeforeClusterItemRendered(MarkerModel store, MarkerOptions markerOptions) {
            //markerOptions.title(store.getStore().getStoreIdentity());
            //if (store.isFavorite()) {
            //    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location_active));
            //} else {
            //    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location_default));
            //}
        }

        @Override
        protected void onBeforeClusterRendered(Cluster<MarkerModel> cluster, MarkerOptions markerOptions) {
            super.onBeforeClusterRendered(cluster, markerOptions);
        }

        @Override
        protected boolean shouldRenderAsCluster(Cluster<MarkerModel> cluster) {
            return cluster.getSize() > 4;
        }

        @Override
        protected int getBucket(Cluster<MarkerModel> cluster) {
            // show exact number of items in cluster's bubble
            return cluster.getSize();
        }
    }

    public void onDataLoaded(List<MarkerModel> markerModels) {
        for (MarkerModel marker : markerModels) {
            clusterManager.addItem(marker);
        }
        clusterManager.cluster();
    }
}
