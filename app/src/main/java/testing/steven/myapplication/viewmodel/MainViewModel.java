package testing.steven.myapplication.viewmodel;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import testing.steven.myapplication.api.ApiRequestManager;
import testing.steven.myapplication.api.ICallback_Notify;
import testing.steven.myapplication.datamodels.OpenDataModel;

public class MainViewModel extends ViewModel {
    int totalSkip = 0;
    int requestPagingAmount = 60;
    MutableLiveData<Boolean> currentlyRequesting;
    protected MutableLiveData<Boolean> pagingEnds;

    private MutableLiveData<ArrayList<OpenDataModel>> openDataLiveData;

    public LiveData<ArrayList<OpenDataModel>> fetchData(Context context) {

        if (openDataLiveData == null) {
            openDataLiveData = new MutableLiveData<>();
        }
        retrieveAPI(context);
        return openDataLiveData;
    }

    public LiveData<Boolean> bindPagingEnds() {

        if (pagingEnds == null) {
            pagingEnds = new MutableLiveData<Boolean>();
            pagingEnds.setValue(false);

        }
        return pagingEnds;
    }

    public LiveData<Boolean> bindRequestIndicator() {

        if (currentlyRequesting == null) {
            currentlyRequesting = new MutableLiveData<Boolean>();
            currentlyRequesting.setValue(false);

        }
        return currentlyRequesting;
    }

    private void retrieveAPI(Context context) {
        if (currentlyRequesting.getValue() != null && !currentlyRequesting.getValue() && pagingEnds.getValue() != null && !pagingEnds.getValue()) {

            Toast.makeText(context, "requestPagingAmount: " + requestPagingAmount + "requestedSkip " + totalSkip, Toast.LENGTH_SHORT).show();
            currentlyRequesting.setValue(true);
            ApiRequestManager.getInstance().getData(context, requestPagingAmount, totalSkip, new ICallback_Notify<ArrayList<OpenDataModel>>() {
                @Override
                public void dataFetched(ArrayList<OpenDataModel> data) {
                    // stop paging request , no more data
                    if (data.size() != requestPagingAmount) {
                        pagingEnds.postValue(true);
                    }
                    // add skip amount
                    totalSkip += requestPagingAmount;
                    ArrayList<OpenDataModel> openDataModels = new ArrayList<>();
                    ArrayList<OpenDataModel> oldValues = openDataLiveData.getValue();
                    // append old datas
                    if (oldValues != null && oldValues.size() > 0) {
                        openDataModels.addAll(oldValues);
                    }
                    openDataModels.addAll(data);
                    currentlyRequesting.postValue(false);
                    openDataLiveData.postValue(openDataModels);

                }

                @Override
                public void failure() {
                    currentlyRequesting.postValue(false);
                    Toast.makeText(context,"APIFailure",Toast.LENGTH_LONG).show();

                }
            });
        }
    }

}
