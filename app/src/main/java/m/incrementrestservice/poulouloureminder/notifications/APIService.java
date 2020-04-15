package m.incrementrestservice.poulouloureminder.notifications;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers({
                "Content-Type:application/json",
                "Authorization:key=AAAApcbhZZg:APA91bETcqnilbmMZC31aRnhfLR2cEIXm_Q8y-4XuDjxnyxoSx0_b_KAopTIl7_obeRwCnJbIb6FRwEsilpl3_CbjIDwJp7x9w--KLP7RgfV2UZTpEbNiOGyfGYuhjXSD-pIWL7VOjCq"

        })

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
