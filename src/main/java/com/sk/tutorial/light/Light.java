package com.sk.tutorial.light;

import org.joml.Vector3f;

import java.util.Vector;

public class Light {

    public Vector3f position;
    public Vector3f ambient;
    public Vector3f diffuse;
    public Vector3f specular;
    public Vector3f direction;

    public float constant;
    public float linear;
    public float quadratic;

    public Light copy() {
        Light light = new Light();
        light.position = new Vector3f(this.position);
        light.ambient = new Vector3f(this.ambient);
        light.diffuse = new Vector3f(this.diffuse);
        light.specular = new Vector3f(this.specular);
        light.direction = new Vector3f(this.direction);
        light.constant = constant;
        light.linear = linear;
        light.quadratic = quadratic;
        return light;
    }
}
