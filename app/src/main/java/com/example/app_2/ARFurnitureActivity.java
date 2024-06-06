package com.example.app_2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Camera;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.ux.ArFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ARFurnitureActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    ArFragment arFragment;
    ExtendedFloatingActionButton button1, button2;
    String str, i;
    HashMap<String,String>modelData = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arfurniture);

        modelData.put("I1","model1.glb");
        modelData.put("I2","model2.glb");
        modelData.put("I3","model3.glb");
        modelData.put("I4","model4.glb");

        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);

        button1.setOnClickListener(this::pop);
        button2.setOnClickListener(this::clearModel);
    }

    private void clearModel(View view) {
        Toast.makeText(this, "All Model Cleared", Toast.LENGTH_SHORT).show();
        List<Node> children = new ArrayList<>(arFragment.getArSceneView().getScene().getChildren());
        for (Node node : children) {
            if (node instanceof AnchorNode) {
                if (((AnchorNode) node).getAnchor() != null) {
                    Objects.requireNonNull(((AnchorNode) node).getAnchor()).detach();
                }
            }
            if (!(node instanceof Camera)) {
                node.setParent(null);
            }
        }
    }

    public void pop (View v){
        PopupMenu popupMenu = new PopupMenu(this,v);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.menu_furniture);
        popupMenu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        i = String.valueOf(item.getTitleCondensed());
        str = modelData.get(i);
        if (modelData.containsKey(i)){
            Toast.makeText(this, str+" selected", Toast.LENGTH_SHORT).show();
            arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.arFragment);
            arFragment.setOnTapPlaneGlbModel(str);
        }
        else {
            Toast.makeText(this, "Plz select a model", Toast.LENGTH_SHORT).show();
        }
        return false;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
}

