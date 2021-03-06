package com.example.finalsemprojectbuyer.billing;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalsemprojectbuyer.Data.ProductDataAfterSale;
import com.example.finalsemprojectbuyer.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class TransactionInvoice extends AppCompatActivity
{
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    RecyclerView rvProducts;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_invoice);
        rvProducts = findViewById(R.id.rv_bill);
        loadProductsDynamically();
    }

    private void loadProductsDynamically()
    {
        final ArrayList<ProductDataAfterSale> products = new ArrayList<>();
        CollectionReference db = FirebaseFirestore.getInstance().collection("BillProducts");
        db.whereEqualTo("product_seller_ID", FirebaseAuth.getInstance().getUid())
                .addSnapshotListener(
                        new EventListener<QuerySnapshot>()
                        {

                            @Override
                            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e)
                            {
                                if (queryDocumentSnapshots != null)
                                {
                                    for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments())
                                    {
                                        ProductDataAfterSale productData = snapshot.toObject(ProductDataAfterSale.class);
                                        products.add(productData);
                                    }
                                    rvProducts.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                    rvProducts.setAdapter(new billAdapter(products));
                                }
                            }

                        });
    }
}
