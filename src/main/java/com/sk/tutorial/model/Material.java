package com.sk.tutorial.model;

import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class Material {

    public List<Texture> textures;
    public Vector3f color;

    public Material() {
        textures = new ArrayList<>();
    }


}
