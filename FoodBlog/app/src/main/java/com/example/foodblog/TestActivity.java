package com.example.foodblog;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodblog.api_instance.RecipeListApiInstance;
import com.example.foodblog.model.IngredientDetail;
import com.example.foodblog.model.Recipe;
import com.example.foodblog.model.RecipeStep;
import com.example.foodblog.model.ResponseObject;

import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TestActivity extends AppCompatActivity {

    private static final String TOKEN = "";

    Button btnTest;
    TextView tvLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        btnTest = findViewById(R.id.btnTest);
        tvLog = findViewById(R.id.tvLog);


        ArrayList<RecipeStep> steps = new ArrayList<>();
        steps.add(new RecipeStep(1, "Step 1"));
        steps.add(new RecipeStep(1, "Step 1"));
        ArrayList<IngredientDetail> ingredients = new ArrayList<>();
//        Recipe recipe = new Recipe("Test", "CK", 4, 20, 10, ArrayList<RecipeStep > steps, ArrayList< IngredientDetail > ingredients);
        btnTest.setOnClickListener(view -> {
//            addRecipe();
        });
    }

//    private void addRecipe(Recipe recipe){
//        RequestBody requestBody = RequestBody.create(MediaType.parse(getContentResolver().getType(recipeListImageUri)), recipeListImageFile);
//        MultipartBody.Part multipartBody = MultipartBody.Part.createFormData("recipeList", recipeListImageFile.getName(), requestBody);
//
//        RequestBody name = RequestBody.create(okhttp3.MultipartBody.FORM, recipeListName);
//
////                RecipeList recipeList = new RecipeList(edtRecipeListName.getText().toString(), multipartBody);
//
//
//        Call<ResponseObject> call = RecipeListApiInstance.getInstance().
//                addRecipeList(MyAuthorization.getInstance().getToken(), name, multipartBody);
////                Call<ResponseObject> call = RecipeListApiInstance.getInstance().
////                        addRecipeList(Authorization.getInstance().getToken(), recipeList);
//        call.enqueue(new Callback<ResponseObject>() {
//            @Override
//            public void onResponse(Call<ResponseObject> call, Response<ResponseObject> response) {
//                if (response.isSuccessful() || response.code() == 200){
//                    setRecipeListData();
//                    Toast.makeText(getContext(), "Thêm danh sách công thức thành công", Toast.LENGTH_SHORT).show();
//                }
//                else {
//                    Toast.makeText(getContext(), "Thêm danh sách công thức thất bại", Toast.LENGTH_SHORT).show();
//                    try {
//                        Log.e("error", response.errorBody().string());
////                        Toast.makeText(getContext(), response.errorBody().string(), Toast.LENGTH_SHORT).show();
//                    }
//                    catch (Exception e){
//                        e.printStackTrace();
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseObject> call, Throwable t) {
//                t.printStackTrace();
//                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
}