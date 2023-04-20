package com.example.foodblog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.foodblog.adapter_recycler_view.IngredientAddAdapter;
import com.example.foodblog.adapter_recycler_view.IngredientSearchAdapter;
import com.example.foodblog.adapter_recycler_view.StepAddAdapter;
import com.example.foodblog.api.ApiUrl;
import com.example.foodblog.api_instance.IngredientApiInstance;
import com.example.foodblog.api_instance.RecipeApiInstance;
import com.example.foodblog.model.Ingredient;
import com.example.foodblog.model.IngredientDetail;
import com.example.foodblog.model.Recipe;
import com.example.foodblog.model.RecipeStep;
import com.example.foodblog.model.ResponseIngredients;
import com.example.foodblog.model.ResponseObject;
import com.example.foodblog.model.ResponseRecipe;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateRecipeActivity extends AppCompatActivity implements RecipeManagerInterface {

    private static final int REQUEST_CODE_SELECT_FROM_GALLERY = 1;
    private static final int REQUEST_CODE_TAKE_NEW_PICTURE = 2;
    private static final int IMAGE_RECIPE_REQUEST = -1;

    // -1 : recipe image; >= 0 : step image
    public static final String TAG_RECIPE_ID = "TAG_RECIPE_ID";
    private int recipeId = -1;
    private int imageViewRequestPosition = IMAGE_RECIPE_REQUEST;
    File recipeImageFile;
    private int stepIndex = 1; // số thứ tự bước của công thức
    private int ingredientSelectedIndex; // vị trí chọn chi tiết nguyên liệu (-1:thêm mới; >=0: chỉnh sửa)
    private IngredientDetail ingredientTemp = null;
    RecyclerView rvIngredientDetail, rvSteps, rvIngredient;
    ArrayList<IngredientDetail> ingredientDetails = new ArrayList<>();
    ArrayList<RecipeStep> recipeSteps = new ArrayList<>();
    ArrayList<Ingredient> ingredientsSearch = new ArrayList<>();
    IngredientAddAdapter ingredientAddAdapter;
    StepAddAdapter stepAddAdapter;
    IngredientSearchAdapter ingredientSearchAdapter;

    ImageView imgChooseImage;
    ImageButton btnBack, btnSaveRecipe, btnBackIngredient, btnSaveIngredient;
    Button btnMoreIngredient, btnMoreStep;
    EditText edtRecipeName, edtRecipeDescription, edtRecipeAmount, edtRecipePrepareTime, edtRecipeCookTime;
    EditText edtIngredientAmount;
    SearchView svIngredient;
    LinearLayout layoutEditRecipe;
    NestedScrollView layoutEditIngredient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_recipe);
        setControl();
        setEvent();
    }

    private void setEvent() {
        btnBack.setOnClickListener(view -> this.finish());
        btnMoreIngredient.setOnClickListener(view -> {
            showFrameEditIngredient();
        });
        btnMoreStep.setOnClickListener(view -> {
            recipeSteps.add(new RecipeStep(stepIndex++));
            stepAddAdapter.notifyItemChanged(recipeSteps.size() - 1);
        });
        imgChooseImage.setOnClickListener(view -> {
            showDialogChooseImage(IMAGE_RECIPE_REQUEST);
        });
        btnSaveRecipe.setOnClickListener(view -> {
            if(isRecipeValidated()){
                showDialogSaveRecipe();
            }
        });
        btnBackIngredient.setOnClickListener(view -> {
            changeLayoutIngredientVisibility();
        });
        btnSaveIngredient.setOnClickListener(view -> {
            if (isIngredientValid()){
                changeLayoutIngredientVisibility();
                if (ingredientSelectedIndex >= 0){
                    ingredientDetails.set(ingredientSelectedIndex, ingredientTemp);
                    ingredientAddAdapter.notifyItemChanged(ingredientSelectedIndex);
                }
                else {
                    ingredientDetails.add(ingredientTemp);
                    ingredientAddAdapter.notifyDataSetChanged();
                }
            }
        });
        svIngredient.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchIngredient(newText);
                return false;
            }
        });
        edtIngredientAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                ingredientTemp.setAmount(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void setControl() {
        rvIngredientDetail = findViewById(R.id.rvIngredientDetail);
        rvIngredientDetail.setAdapter(ingredientAddAdapter);
        rvIngredientDetail.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

        btnMoreIngredient = findViewById(R.id.btnMoreIngredient);

        rvSteps = findViewById(R.id.rvCookingStep);
        rvSteps.setAdapter(stepAddAdapter);
        rvSteps.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

        btnMoreStep = findViewById(R.id.btnMoreStep);

        rvIngredient = findViewById(R.id.rvIngredient);
        rvIngredient.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        rvIngredient.setAdapter(ingredientSearchAdapter);

        btnSaveRecipe = findViewById(R.id.btnSaveRecipe);
        imgChooseImage = findViewById(R.id.imgChooseImage);
        edtRecipeName = findViewById(R.id.edtRecipeName);
        edtRecipeDescription = findViewById(R.id.edtRecipeDescription);
        edtRecipeAmount = findViewById(R.id.edtAmount);
        edtRecipePrepareTime = findViewById(R.id.edtPrepareTime);
        edtRecipeCookTime = findViewById(R.id.edtCookTime);

        btnBack = findViewById(R.id.btnBack);
        btnBackIngredient = findViewById(R.id.btnBackIngredient);
        btnSaveIngredient = findViewById(R.id.btnSaveIngredient);
        layoutEditRecipe = findViewById(R.id.layoutEditRecipe);
        layoutEditIngredient = findViewById(R.id.layoutEditIngredient);
        svIngredient = findViewById(R.id.searchViewIngredient);
        edtIngredientAmount = findViewById(R.id.edtIngredientAmount);

        setData();
    }

    private void setData() {
//        ingredientDetails = new ArrayList<>();
//        ingredientAddAdapter = new IngredientAddAdapter(this, ingredientDetails);
//
//        recipeSteps = new ArrayList<>();
//        recipeSteps.add(new RecipeStep(stepIndex++));
//        recipeSteps.add(new RecipeStep(stepIndex++));
//        stepAddAdapter = new StepAddAdapter(this, recipeSteps);

        Intent intent = getIntent();
        recipeId = intent.getIntExtra(TAG_RECIPE_ID, -1);
        if (recipeId != -1){
            setRecipeData();
        }
    }

    private void setRecipeData() {
        Call<ResponseRecipe> call = RecipeApiInstance.getInstance().getRecipeById(MyAuthorization.getInstance().getToken(), recipeId);
        call.enqueue(new Callback<ResponseRecipe>() {
            @Override
            public void onResponse(Call<ResponseRecipe> call, Response<ResponseRecipe> response) {
                if (response.isSuccessful() || response.code() == 200){
                    edtRecipeName.setText(response.body().getData().getRecipeName());
                    edtRecipeDescription.setText(response.body().getData().getDescription());
                    edtRecipeAmount.setText(String.valueOf(response.body().getData().getAmount()));
                    edtRecipePrepareTime.setText(String.valueOf(response.body().getData().getPreparationTime()));
                    edtRecipeCookTime.setText(String.valueOf(response.body().getData().getCookingTime()));
                    Picasso.get().load(ApiUrl.IMAGE_URL + response.body().getData().getImage()).into(imgChooseImage);

                    ingredientDetails.addAll(response.body().getData().getIngredients());
                    ingredientAddAdapter = new IngredientAddAdapter(UpdateRecipeActivity.this, ingredientDetails);
                    rvIngredientDetail.setAdapter(ingredientAddAdapter);

                    recipeSteps.addAll(response.body().getData().getSteps());
                    stepAddAdapter = new StepAddAdapter(UpdateRecipeActivity.this, recipeSteps);
                    rvSteps.setAdapter(stepAddAdapter);

                    stepIndex = recipeSteps.size() + 1;
                }
                else if (response.code() == 432){
                    Toast.makeText(UpdateRecipeActivity.this, R.string.error_recipe_432, Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(UpdateRecipeActivity.this, R.string.error_500, Toast.LENGTH_SHORT).show();
                    try {
                        Log.e("error", response.errorBody().string());
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseRecipe> call, Throwable t) {
                Toast.makeText(UpdateRecipeActivity.this, R.string.error_500, Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case MyPermission.REQUEST_CODE_WRITE_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    selectPictureFromGallery();
                }
                else {
                    Toast.makeText(this, R.string.warning_grant_permission_access_storage, Toast.LENGTH_SHORT).show();
                }
                break;
            case MyPermission.REQUEST_CODE_CAMERA_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    takePictureFromCamera();
                }
                else {
                    Toast.makeText(this, R.string.warning_grant_permission_camera, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_CODE_SELECT_FROM_GALLERY:
                if (resultCode == RESULT_OK){
                    Uri imgUri = data.getData();
                    if (imageViewRequestPosition == IMAGE_RECIPE_REQUEST){
                        recipeImageFile = new File(MyFileReader.getRealPathFromURI(this, imgUri));
                        imgChooseImage.setImageURI(imgUri);
                    }
                    else {
                        recipeSteps.get(imageViewRequestPosition).setImageUri(imgUri);
                        recipeSteps.get(imageViewRequestPosition).setImageFile(new File(MyFileReader.getRealPathFromURI(this, imgUri)));
                        recipeSteps.get(imageViewRequestPosition).setImage("has_image");
                        focusRvStepsEdittext(0);
                        stepAddAdapter.notifyDataSetChanged();
                    }
                }
                break;
            case REQUEST_CODE_TAKE_NEW_PICTURE:
                if (resultCode == RESULT_OK){
                    Bundle bundle = data.getExtras();
                    Bitmap newTakenImage = (Bitmap) bundle.get("data");
                    imgChooseImage.setImageBitmap(newTakenImage);
                }
                break;
        }
    }

    public void showDialogChooseImage(int imageViewRequest){
        this.imageViewRequestPosition = imageViewRequest;
        // chỉ chọn ảnh từ thư viện
        if (MyPermission.checkReadFilePermission(this)){
            selectPictureFromGallery();
        }
        else {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MyPermission.REQUEST_CODE_WRITE_EXTERNAL_STORAGE);
        }
        // cho phép user lự chọn giữa chọn ảnh từ thư viện hoặc chụp ảnh
//        Dialog dialog = new Dialog(this);
//        dialog.setContentView(R.layout.dialog_choose_image_option);
//        ImageButton btnTakePicture = dialog.findViewById(R.id.btnTakePicture);
//        ImageButton btnSelectFromGallery = dialog.findViewById(R.id.btnSelectFromGallery);
//        btnTakePicture.setOnClickListener(view -> {
//            if (MyPermission.checkCameraPermission(this)){
//                takePictureFromCamera();
//            }
//            else {
//                requestPermissions(new String[]{android.Manifest.permission.CAMERA}, MyPermission.REQUEST_CODE_CAMERA_PERMISSION);
//            }
//            dialog.dismiss();
//        });
//        btnSelectFromGallery.setOnClickListener(view -> {
//            if (MyPermission.checkReadFilePermission(this)){
//                selectPictureFromGallery();
//            }
//            else {
//                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MyPermission.REQUEST_CODE_WRITE_EXTERNAL_STORAGE);
//            }
//            dialog.dismiss();
//        });
//        dialog.show();
    }

    private void selectPictureFromGallery(){
        Intent selectImageFromGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(selectImageFromGalleryIntent, REQUEST_CODE_SELECT_FROM_GALLERY);
    }

    private void takePictureFromCamera(){
        Intent takePictureFromCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureFromCameraIntent, REQUEST_CODE_TAKE_NEW_PICTURE);
    }

    private boolean isRecipeValidated(){
//        if (recipeImageFile == null){
//            Toast.makeText(this, "Please choose recipe image", Toast.LENGTH_SHORT).show();
//            return false;
//        }
        if (edtRecipeName.getText().toString().equals("")){
            edtRecipeName.setError(getText(R.string.error_miss_recipe_name));
            edtRecipeName.requestFocus();
            return false;
        }
        if (ingredientDetails.size() < 1){
            Toast.makeText(this, R.string.error_miss_recipe_ingredient, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (recipeSteps.size() < 1){
            Toast.makeText(this, R.string.error_miss_recipe_step, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void showDialogSaveRecipe(){
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_save_recipe);
        // ánh xạ view
        LinearLayout layout = dialog.findViewById(R.id.layout);
        Button btnSubmit = dialog.findViewById(R.id.btnSubmit);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        RadioButton radPrivate = dialog.findViewById(R.id.radPrivate);

        // set event
        try { // Adjust dialog width fit to screen
            ViewGroup.LayoutParams params = layout.getLayoutParams();
            params.width = (int)(getResources().getDisplayMetrics().widthPixels*0.90);
            layout.requestLayout();
        }
        catch (Exception e){
            e.printStackTrace();
        }

        btnSubmit.setOnClickListener(view -> {
            dialog.dismiss();
            updateRecipe(radPrivate.isChecked());
        });

        btnCancel.setOnClickListener(view -> {
            dialog.dismiss();
        });

        dialog.show();
    }

    private void updateRecipe(boolean isRecipePrivate) {
        try {
            String recipeName = edtRecipeName.getText().toString();
            String recipeDescription = edtRecipeDescription.getText().toString();
            String status = isRecipePrivate ? Recipe.STATUS_PRIVATE : Recipe.STATUS_PUBLIC;
            int amount = Integer.parseInt(edtRecipeAmount.getText().toString());
            int prepareTime = Integer.parseInt(edtRecipePrepareTime.getText().toString());
            int cookTime = Integer.parseInt(edtRecipeCookTime.getText().toString());

            ArrayList<File> imageStepFiles = new ArrayList<>();
            for (int i=0; i<recipeSteps.size(); i++){
                if (recipeSteps.get(i).getImageFile() != null){
                    imageStepFiles.add(recipeSteps.get(i).getImageFile());
                }
            }

            Recipe recipe = new Recipe(recipeName, recipeDescription, status, amount, prepareTime, cookTime, recipeSteps, ingredientDetails);
            String jsonBody = new Gson().toJson(recipe);
            RequestBody dataBody = RequestBody.create(MediaType.parse("application/json"), jsonBody);
            Log.e("test", jsonBody);
            Log.e("test", "imageFileCount = " + imageStepFiles.size());

            MultipartBody.Builder builder = new MultipartBody.Builder();
            builder.setType(MultipartBody.FORM);


            MultipartBody.Part multipartBodyRecipe = null;
            if (recipeImageFile != null){
                RequestBody requestBodyRecipe = RequestBody.create(MediaType.parse("image/jpeg"), recipeImageFile);
                multipartBodyRecipe = MultipartBody.Part.createFormData(
                        "recipe",
                        recipeImageFile.getName(),
                        requestBodyRecipe);
            }


            MultipartBody.Part[] multipartBodySteps = new MultipartBody.Part[imageStepFiles.size()];
            for (int i=0; i<imageStepFiles.size(); i++){
                RequestBody requestBodyStep = RequestBody.create(MediaType.parse("image/jpeg"), imageStepFiles.get(i));
                multipartBodySteps[i] = MultipartBody.Part.createFormData(
                        "step",
                        imageStepFiles.get(i).getName(),
                        requestBodyStep);
            }

            Call<ResponseObject> call = RecipeApiInstance.getInstance().updateRecipe(
                    MyAuthorization.getInstance().getToken(),
                    recipeId,
                    dataBody,
                    multipartBodyRecipe,
                    multipartBodySteps
            );
            call.enqueue(new Callback<ResponseObject>() {
                @Override
                public void onResponse(Call<ResponseObject> call, Response<ResponseObject> response) {
                    if (response.isSuccessful() || response.code() == 200){
                        Toast.makeText(UpdateRecipeActivity.this, R.string.success_update_recipe, Toast.LENGTH_SHORT).show();
                        UpdateRecipeActivity.this.finish();
                    }
                    else {
                        Toast.makeText(UpdateRecipeActivity.this, R.string.error_500, Toast.LENGTH_SHORT).show();
                        try {
                            Log.e("error", response.errorBody().string());
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseObject> call, Throwable t) {
                    Toast.makeText(UpdateRecipeActivity.this, R.string.error_500, Toast.LENGTH_SHORT).show();
                    t.printStackTrace();
                }
            });
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    public void focusRvStepsEdittext(int position){
        rvSteps.getLayoutManager().findViewByPosition(position).findViewById(R.id.edtStepDescription).requestFocus();
    }

    public void showFrameEditIngredient(int ingredientPosition){
        Log.e("test", "edit");
        ingredientSelectedIndex = ingredientPosition;
        ingredientTemp = new IngredientDetail(
                ingredientDetails.get(ingredientSelectedIndex).getIngredientId(),
                ingredientDetails.get(ingredientSelectedIndex).getName(),
                ingredientDetails.get(ingredientSelectedIndex).getAmount());
        changeLayoutIngredientVisibility();
        svIngredient.setQuery(ingredientTemp.getName(), false);
        edtIngredientAmount.setText(ingredientTemp.getAmount());
    }

    @Override
    public void toastDeleteIngredient() {
        Toast.makeText(this, R.string.error_miss_recipe_ingredient, Toast.LENGTH_SHORT).show();
    }

    @Override
    public int getStepIndex() {
        return stepIndex;
    }

    @Override
    public void setStepIndex(int index) {
        stepIndex = index;
    }

    @Override
    public String getMyText(int resId) {
        return getText(resId).toString();
    }

    @Override
    public void toastDeleteStep() {
        Toast.makeText(this, R.string.error_miss_recipe_step, Toast.LENGTH_SHORT).show();
    }

    public void showFrameEditIngredient(){
        Log.e("test", "add");
        ingredientSelectedIndex = -1;
        ingredientTemp = new IngredientDetail();
        svIngredient.setQuery("", true);
        edtIngredientAmount.setText("");
        changeLayoutIngredientVisibility();
    }

    private void changeLayoutIngredientVisibility(){
        if (layoutEditIngredient.getVisibility() == View.VISIBLE){
            layoutEditIngredient.setVisibility(View.GONE);
            layoutEditRecipe.setVisibility(View.VISIBLE);
        }
        else {
            layoutEditIngredient.setVisibility(View.VISIBLE);
            layoutEditRecipe.setVisibility(View.GONE);
        }
    }

    private void searchIngredient(String searchKey) {
        if (searchKey.equals("")){
            ingredientsSearch.clear();
            ingredientSearchAdapter = new IngredientSearchAdapter(UpdateRecipeActivity.this, ingredientsSearch);
            rvIngredient.setAdapter(ingredientSearchAdapter);
        }
        else {
            Call<ResponseIngredients> call = IngredientApiInstance.getInstance().searchIngredient(MyAuthorization.getInstance().getToken(), searchKey);
            call.enqueue(new Callback<ResponseIngredients>() {
                @Override
                public void onResponse(Call<ResponseIngredients> call, Response<ResponseIngredients> response) {
                    if (response.isSuccessful() || response.code() == 200){
                        ingredientsSearch.clear();
                        ingredientsSearch.addAll(response.body().getData());
                        ingredientSearchAdapter = new IngredientSearchAdapter(UpdateRecipeActivity.this, ingredientsSearch);
                        rvIngredient.setAdapter(ingredientSearchAdapter);
                    }
                    else if (response.code() == 400){
                        ingredientsSearch.clear();
                        ingredientSearchAdapter = new IngredientSearchAdapter(UpdateRecipeActivity.this, ingredientsSearch);
                        rvIngredient.setAdapter(ingredientSearchAdapter);
                    }
                    else {
                        Toast.makeText(UpdateRecipeActivity.this, R.string.error_500, Toast.LENGTH_SHORT).show();
                        try {
                            Log.e("error", response.errorBody().string());
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseIngredients> call, Throwable t) {
                    Toast.makeText(UpdateRecipeActivity.this, R.string.error_500, Toast.LENGTH_SHORT).show();
                    t.printStackTrace();
                }
            });
        }
    }

    public void setSelectIngredient(int ingredientPosition){
        svIngredient.setQuery(ingredientsSearch.get(ingredientPosition).getName(), true);
        ingredientTemp.setIngredientId(ingredientsSearch.get(ingredientPosition).getIngredientId());
        ingredientTemp.setName(ingredientsSearch.get(ingredientPosition).getName());
    }

    private boolean isIngredientValid(){
        if (ingredientTemp.getName().equals("")){
            Toast.makeText(this, R.string.error_recipe_ingredient_invalid, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (ingredientTemp.getAmount().equals("")){
            Toast.makeText(this, R.string.error_miss_ingredient_amout, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}