//package com.example.app_2;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.fragment.app.Fragment;
//import androidx.fragment.app.FragmentManager;
//
//import android.net.Uri;
//import android.os.Bundle;
//import android.widget.Toast;
//
//import com.google.ar.core.AugmentedFace;
//import com.google.ar.sceneform.ArSceneView;
//import com.google.ar.sceneform.Sceneform;
//import com.google.ar.sceneform.rendering.ModelRenderable;
//import com.google.ar.sceneform.rendering.Renderable;
//import com.google.ar.sceneform.rendering.RenderableInstance;
//import com.google.ar.sceneform.rendering.Texture;
//import com.google.ar.sceneform.ux.ArFrontFacingFragment;
//import com.google.ar.sceneform.ux.AugmentedFaceNode;
//
//import java.util.HashMap;
//
//public class MakeupActivity extends AppCompatActivity {
//
//    ModelRenderable faceModel;
//    Texture faceTexture;
//    ArSceneView arSceneView;
//    ArFrontFacingFragment arFragment;
//    HashMap<AugmentedFace, AugmentedFaceNode> facesNodes = new HashMap<>();
//    String str="fox.glb";
//    String str2="Untitled design.png";
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_makeup);
//
//        getSupportFragmentManager().addFragmentOnAttachListener(this::onAttachFragment);
//
//        if (savedInstanceState == null) {
//            if (Sceneform.isSupported(this)) {
//                getSupportFragmentManager().beginTransaction()
//                        .add(R.id.arFragment, ArFrontFacingFragment.class, null)
//                        .commit();
//            }
//        }
//
//        loadModel();
//        loadTexture();
//
//    }
//
//    private void loadModel() {
//
//        ModelRenderable.builder()
//                .setSource(this, Uri.parse(str))
//                .setIsFilamentGltf(true)
//                .build()
//                .thenAccept(model -> faceModel = model)
//                .exceptionally(throwable -> {
//                    Toast.makeText(this, "Unable to load renderable", Toast.LENGTH_LONG).show();
//                    return null;
//                });
//    }
//
//    private void loadTexture() {
//
//        Texture.builder()
//                .setSource(this,Uri.parse(str2))
//                .setUsage(Texture.Usage.COLOR_MAP)
//                .build()
//                .thenAccept(texture -> faceTexture = texture)
//                .exceptionally(throwable -> {
//                    Toast.makeText(this, "Unable to load texture", Toast.LENGTH_LONG).show();
//                    return null;
//                });
//    }
//
//    private void onAttachFragment(FragmentManager fragmentManager, Fragment fragment) {
//        if (fragment.getId() == R.id.arFragment) {
//            arFragment = (ArFrontFacingFragment) fragment;
//            arFragment.setOnViewCreatedListener(this::onViewCreated);
//        }
//    }
//
//    private void onViewCreated(ArSceneView arSceneView) {
//        this.arSceneView = arSceneView;
//        arSceneView.setCameraStreamRenderPriority(Renderable.RENDER_PRIORITY_FIRST);
//        // Check for face detections
//        arFragment.setOnAugmentedFaceUpdateListener(this::onAugmentedFaceTrackingUpdate);
//    }
//
//    private void onAugmentedFaceTrackingUpdate(AugmentedFace augmentedFace) {
//        if (faceModel == null || faceTexture == null) {
//            return;
//        }
//
//        AugmentedFaceNode existingFaceNode = facesNodes.get(augmentedFace);
//
//        switch (augmentedFace.getTrackingState()) {
//            case TRACKING:
//                if (existingFaceNode == null) {
//                    AugmentedFaceNode faceNode = new AugmentedFaceNode(augmentedFace);
//
//                    RenderableInstance modelInstance = faceNode.setFaceRegionsRenderable(faceModel);
//                    modelInstance.setShadowCaster(false);
//                    modelInstance.setShadowReceiver(true);
//
//                    faceNode.setFaceMeshTexture(faceTexture);
//
//                    arSceneView.getScene().addChild(faceNode);
//
//                    facesNodes.put(augmentedFace, faceNode);
//                }
//                break;
//            case STOPPED:
//                if (existingFaceNode != null) {
//                    arSceneView.getScene().removeChild(existingFaceNode);
//                }
//                facesNodes.remove(augmentedFace);
//                break;
//        }
//    }
//}