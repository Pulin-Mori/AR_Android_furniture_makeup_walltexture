package com.example.app_2;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.google.ar.core.Anchor;
import com.google.ar.core.Config;
import com.google.ar.core.Frame;
import com.google.ar.core.Plane;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.PlaneRenderer;
import com.google.ar.sceneform.rendering.Texture;
import com.google.ar.sceneform.ux.ArFragment;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class WalltextureActivity extends AppCompatActivity {

    ArFragment arFragment;
    Plane plane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walltexture);

        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.arFragment);
        arFragment.getArSceneView().getScene().addOnUpdateListener(this::onUpdate);

//        arFragment.setOnTapArPlaneListener(((hitResult, plane1, motionEvent) -> {
//            if (plane1.getType() == Plane.Type.VERTICAL) {
//                if (hitResult != null) {
//                    renderWallTexture(arFragment, hitResult.createAnchor());
//                }
//            }
//        }));

    }

    //    private void renderWallTexture(ArFragment arFragment, Anchor anchor) {
//
//        ModelRenderable.builder()
//                .setSource(this, Uri.parse("texture1.jpeg"))
//                .build()
//                .thenAccept(modelRenderable -> {
//                            modelRenderable.setShadowCaster(false);
//                                    modelRenderable.setShadowReceiver(false);
//                                    addNodeToScene(arFragment, anchor, modelRenderable);}
//                );
//    }
//
//    private Object addNodeToScene(ArFragment arFragment, Anchor anchor, ModelRenderable modelRenderable) {
//
//        Node node = new Node();
//        node.setRenderable(modelRenderable);
//        AnchorNode anchorNode = new AnchorNode(anchor);
//        anchorNode.addChild(node);
//        arFragment.getArSceneView().getScene().addChild(anchorNode);
//
//        return null;
//    }
    private void onUpdate(FrameTime frameTime) {

        Frame frame = arFragment.getArSceneView().getArFrame();
        Collection<Plane> planes = frame.getUpdatedTrackables(Plane.class);

        for (Plane plane : planes) {

            if (plane.getTrackingState() == TrackingState.TRACKING && plane.getType() == Plane.Type.VERTICAL) {

                Texture.Sampler sampler = Texture.Sampler.builder()
                        .setMinFilter(Texture.Sampler.MinFilter.LINEAR)
                        .setMagFilter(Texture.Sampler.MagFilter.LINEAR)
                        .setWrapMode(Texture.Sampler.WrapMode.REPEAT).build();

                // Build texture with sampler
                CompletableFuture<Texture> trigrid = Texture.builder()
                        .setSource(this, Uri.parse("texture1.jpeg"))
                        .setSampler(sampler).build();

                // Set plane texture
                arFragment.getArSceneView()
                        .getPlaneRenderer()
                        .getMaterial()
                        .thenAcceptBoth(trigrid, (material, texture) -> {
                            material.setTexture(PlaneRenderer.MATERIAL_TEXTURE, texture);
                        });

            }
        }
    }
}