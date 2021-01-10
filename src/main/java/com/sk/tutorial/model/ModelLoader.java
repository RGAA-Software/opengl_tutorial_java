package com.sk.tutorial.model;

import com.sk.tutorial.shader.ShaderProgram;
import com.sk.tutorial.util.TextUtils;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.assimp.AIColor4D;
import org.lwjgl.assimp.AIFace;
import org.lwjgl.assimp.AIMaterial;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.AIString;
import org.lwjgl.assimp.AIVector3D;
import org.lwjgl.assimp.Assimp;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public class ModelLoader {

    public static Model loadModel(String path, ShaderProgram shaderProgram) {
        String basePath = path.substring(0, path.lastIndexOf("/"));
        AIScene aiScene = Assimp.aiImportFile(path, Assimp.aiProcess_Triangulate/*|Assimp.aiProcess_GenNormals|Assimp.aiProcess_FlipUVs*/ | Assimp.aiProcess_CalcTangentSpace);

        System.out.println("materials : " + aiScene.mNumMaterials());
        System.out.println("meshs : " + aiScene.mNumMeshes());

        assert (aiScene.mNumMaterials() == aiScene.mNumMeshes());

        Model model = new Model(shaderProgram);
        model.meshes = new ArrayList<>();

        List<Material> materials = new ArrayList<>();
        if (aiScene.mMaterials() != null) {
            for (int i = 0; i < aiScene.mNumMaterials(); i++) {
                AIMaterial aiMaterial = AIMaterial.create(aiScene.mMaterials().get(i));
                Material material = processMaterial(basePath, aiMaterial);
                materials.add(material);
            }
        }

        if (aiScene.mMeshes() != null) {
            for (int i = 0; i < aiScene.mNumMeshes(); i++) {
                AIMesh aiMesh = AIMesh.create(aiScene.mMeshes().get(i));
                int materialIndex = aiMesh.mMaterialIndex();
                Mesh mesh = processMesh(aiMesh, materials.get(materialIndex), shaderProgram);
                model.meshes.add(mesh);
            }
        }

        return model;
    }

    private static Mesh processMesh(AIMesh aiMesh, Material material, ShaderProgram shaderProgram) {
        Mesh mesh;

        List<Vertex> vertexList = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();

        List<Vector3f> vertices = new ArrayList<>();
        List<Vector2f> texCoords = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();
        List<Vector3f> tangents = new ArrayList<>();
        List<Vector3f> bitangents = new ArrayList<>();

        AIVector3D.Buffer aiVertices = aiMesh.mVertices();
        while (aiVertices.remaining() > 0) {
            AIVector3D aiVertex = aiVertices.get();
            vertices.add(new Vector3f(aiVertex.x(),aiVertex.y(), aiVertex.z()));
        }

        AIVector3D.Buffer aiTexCoords = aiMesh.mTextureCoords(0);
        if (aiTexCoords != null){
            while (aiTexCoords.remaining() > 0) {
                AIVector3D aiTexCoord = aiTexCoords.get();
                texCoords.add(new Vector2f(aiTexCoord.x(),aiTexCoord.y()));
            }
        }

        AIVector3D.Buffer aiNormals = aiMesh.mNormals();
        if (aiNormals != null){
            while (aiNormals.remaining() > 0) {
                AIVector3D aiNormal = aiNormals.get();
                normals.add(new Vector3f(aiNormal.x(),aiNormal.y(),aiNormal.z()));
            }
        }
        AIVector3D.Buffer aiTangents = aiMesh.mTangents();
        if (aiTangents != null){
            while (aiTangents.remaining() > 0) {
                AIVector3D aiTangent = aiTangents.get();
                tangents.add(new Vector3f(aiTangent.x(),aiTangent.y(),aiTangent.z()));
            }
            System.out.println("tangents size : " + tangents.size());
        }

        AIVector3D.Buffer aiBitangents = aiMesh.mBitangents();
        if (aiBitangents != null){
            while (aiBitangents.remaining() > 0) {
                AIVector3D aiBitangent = aiBitangents.get();
                bitangents.add(new Vector3f(aiBitangent.x(),aiBitangent.y(),aiBitangent.z()));
            }
            System.out.println("bitangents size : " + bitangents.size());
        }

        AIFace.Buffer aifaces = aiMesh.mFaces();
        while (aifaces.remaining() > 0) {
            AIFace aiface = aifaces.get();

            if (aiface.mNumIndices() == 3) {
                IntBuffer indicesBuffer = aiface.mIndices();
                indices.add(indicesBuffer.get(0));
                indices.add(indicesBuffer.get(1));
                indices.add(indicesBuffer.get(2));
            }
            if (aiface.mNumIndices() == 4) {
                IntBuffer indicesBuffer = aiface.mIndices();
                indices.add(indicesBuffer.get(0));
                indices.add(indicesBuffer.get(1));
                indices.add(indicesBuffer.get(2));
                indices.add(indicesBuffer.get(0));
                indices.add(indicesBuffer.get(1));
                indices.add(indicesBuffer.get(3));
                indices.add(indicesBuffer.get(1));
                indices.add(indicesBuffer.get(2));
                indices.add(indicesBuffer.get(3));
            }

        }

        for(int i=0; i<vertices.size(); i++){
            Vertex vertex = new Vertex();
            vertex.position = vertices.get(i);
            if (!normals.isEmpty()){
                vertex.normal = normals.get(i);
            }
            else{
                vertex.normal = new Vector3f(0,0,0);
            }
            if (!texCoords.isEmpty()){
                vertex.texCoords = texCoords.get(i);
            }
            else{
                vertex.texCoords = new Vector2f(0,0);
            }
            if (!tangents.isEmpty()){
                vertex.tangent = tangents.get(i);
            }
            if (!bitangents.isEmpty()){
                vertex.bitangent = bitangents.get(i);
            }
            vertexList.add(vertex);
        }

        mesh = new Mesh(vertexList, indices, material, shaderProgram);

        return mesh;
    }


    private static Material processMaterial(String basePath, AIMaterial aiMaterial) {
        Material material = new Material();
        // diffuse Texture
        AIString diffPath = AIString.calloc();
        Assimp.aiGetMaterialTexture(aiMaterial, Assimp.aiTextureType_DIFFUSE, 0, diffPath, (IntBuffer) null, null, null, null, null, null);
        String diffTexPath = diffPath.dataString();
        System.out.println("diffTex path : " + diffTexPath);

        Texture diffuseTexture;
        if (!TextUtils.isEmpty(diffTexPath)) {
            diffuseTexture = new Texture(basePath + "/" + diffTexPath, Texture.TYPE_DIFFUSE);
            material.textures.add(diffuseTexture);
        }

        // normal Texture
        AIString normalPath = AIString.calloc();
        Assimp.aiGetMaterialTexture(aiMaterial, Assimp.aiTextureType_HEIGHT, 0, normalPath, (IntBuffer) null, null, null, null, null, null);
        String normalTexPath = normalPath.dataString();
        System.out.println("normalTex path : " + normalTexPath);

        Texture normalTexture;
        if (!TextUtils.isEmpty(normalTexPath)) {
            normalTexture = new Texture(basePath + "/" + normalTexPath, Texture.TYPE_NORMAL);
            material.textures.add(normalTexture);
        }

        AIColor4D color = AIColor4D.create();

        Vector3f diffuseColor = null;
        int result = Assimp.aiGetMaterialColor(aiMaterial, Assimp.AI_MATKEY_COLOR_AMBIENT, Assimp.aiTextureType_NONE, 0, color);
        if (result == 0) {
            diffuseColor = new Vector3f(color.r(), color.g(), color.b());
            material.color = diffuseColor;
            System.out.println("diffuse color : r : " + diffuseColor.x + " g : " + diffuseColor.y + " b : " + diffuseColor.z);
        }

        AIString specularPath = AIString.calloc();
        Assimp.aiGetMaterialTexture(aiMaterial, Assimp.aiTextureType_SPECULAR, 0, specularPath, (IntBuffer) null, null, null, null, null, null);
        String specularTexPath = specularPath.dataString();

        Texture specularTexture;
        if (!TextUtils.isEmpty(specularTexPath)) {
            specularTexture = new Texture(basePath + "/" + specularTexPath, Texture.TYPE_SPECULAR);
            material.textures.add(specularTexture);
        }
        System.out.println("specular path : " + specularTexPath);
        System.out.println("----------------------------------------------");

        return material;
    }

}
