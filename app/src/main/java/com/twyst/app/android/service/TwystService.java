package com.twyst.app.android.service;

import com.twyst.app.android.model.AuthToken;
import com.twyst.app.android.model.BaseResponse;
import com.twyst.app.android.model.Cashback;
import com.twyst.app.android.model.CheckinData;
import com.twyst.app.android.model.Data;
import com.twyst.app.android.model.DiscoverData;
import com.twyst.app.android.model.Feedback;
import com.twyst.app.android.model.Friend;
import com.twyst.app.android.model.GrabOffer;
import com.twyst.app.android.model.LikeOffer;
import com.twyst.app.android.model.LocationData;
import com.twyst.app.android.model.LocationDetails.LocationsVerified;
import com.twyst.app.android.model.LocationDetails.LocationsVerify;
import com.twyst.app.android.model.NotificationData;
import com.twyst.app.android.model.OTPCode;
import com.twyst.app.android.model.Offer;
import com.twyst.app.android.model.OrderHistory;
import com.twyst.app.android.model.OrderUpdate;
import com.twyst.app.android.model.OutletDetailData;
import com.twyst.app.android.model.Profile;
import com.twyst.app.android.model.ProfileUpdate;
import com.twyst.app.android.model.Referral;
import com.twyst.app.android.model.ReportProblem;
import com.twyst.app.android.model.ShareOffer;
import com.twyst.app.android.model.ShareOutlet;
import com.twyst.app.android.model.SubmitOffer;
import com.twyst.app.android.model.Suggestion;
import com.twyst.app.android.model.UpdateProfile;
import com.twyst.app.android.model.UploadBill;
import com.twyst.app.android.model.UseOffer;
import com.twyst.app.android.model.UserLocation;
import com.twyst.app.android.model.Voucher;
import com.twyst.app.android.model.WalletData;
import com.twyst.app.android.model.WriteToUs;
import com.twyst.app.android.model.menu.MenuData;
import com.twyst.app.android.model.order.CancelOrder;
import com.twyst.app.android.model.order.OrderCheckOut;
import com.twyst.app.android.model.order.OrderCheckOutResponse;
import com.twyst.app.android.model.order.OrderConfirmedCOD;
import com.twyst.app.android.model.order.OrderSummary;
import com.twyst.app.android.offer.FoodOffer;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by satishk on 5/6/15.
 */
public interface TwystService {
    @GET("/api/v4/authcode/{mobileno}")
    public void getMobileAuthCode(@Path("mobileno") String mobile, Callback<BaseResponse<OTPCode>> callback);

    @GET("/api/v4/menus/{menu_id}")
    public void getMenu(@Path("menu_id") String menuID, @Query("token") String token, Callback<BaseResponse<MenuData>> callback);

    @POST("/api/v4/order/verify")
    public void postOrderVerify(@Query("token") String token, @Body() OrderSummary orderSummary, Callback<BaseResponse<OrderSummary>> callback);

    @POST("/api/v4/order/apply/offer")
    public void postOfferApply(@Query("token") String token, @Body() OrderSummary orderSummary, Callback<BaseResponse<OrderSummary>> callback);

    @POST("/api/v4/order/checkout")
    public void postOrderCheckOut(@Query("token") String token, @Body() OrderCheckOut orderCheckOut, Callback<BaseResponse<OrderCheckOutResponse>> callback);

    @POST("/api/v4/order/confirm")
    public void postOrderConfirmCOD(@Query("token") String token, @Body() OrderConfirmedCOD orderConfirmedCOD, Callback<BaseResponse> callback);

    @FormUrlEncoded
    @POST("/api/v4/authcode")
    public void userAuthToken(@Field("code") String code, @Field("phone") String phone, Callback<BaseResponse<AuthToken>> callback);

    @PUT("/api/v4/profile")
    public void updateProfile(@Query("token") String token, @Body UpdateProfile updateProfile, Callback<BaseResponse<ProfileUpdate>> callback);

    @GET("/api/v4/delivery/recos")
    public void getRecommendedOutlets(@Query("token") String token, @Query("lat") String lat, @Query("long") String lng, @Query("date") String date, @Query(value = "time", encodeValue = false) String time, Callback<BaseResponse<DiscoverData>> callback);

    @GET("/api/v4/outlets/{outlet_id}")
    public void getOutletDetails(@Path("outlet_id") String outletId, @Query("token") String token, @Query("lat") String lat, @Query("long") String lng, Callback<BaseResponse<OutletDetailData>> callback);

    @GET("/api/v4/locations/outlets")
    public void getOutletsList(Callback<BaseResponse> callback);

    @GET("/api/v4/orders")
    public void getOrderHistory(@Query("token") String token, Callback<BaseResponse<ArrayList<OrderHistory>>> callback);

    @GET("/api/v4/order/{order_id}")
    public void getOrderDetail(@Path("order_id") String orderId, @Query("token") String token, Callback<BaseResponse<OrderHistory>> callback);

    @POST("/api/v4/user/cancel_order")
    public void postOrderCancel(@Query("token") String token, @Body() CancelOrder cancelOrder, Callback<BaseResponse> callback);

    @PUT("/api/v4/friends")
    public void updateSocialFriends(@Query("token") String token, @Body() Friend friend, Callback<BaseResponse<ProfileUpdate>> callback);

    @FormUrlEncoded
    @POST("/api/v4/outlet/follow")
    public void followEvent(@Query("token") String token, @Field("event_outlet") String outletId, Callback<BaseResponse<Data>> callback);

    @FormUrlEncoded
    @POST("/api/v4/outlet/unfollow")
    public void unFollowEvent(@Query("token") String token, @Field("event_outlet") String outletId, Callback<BaseResponse<Data>> callback);

    @POST("/api/v4/outlet/feedback")
    public void outletFeedback(@Query("token") String token, @Body() Feedback feedback, Callback<BaseResponse> callback);

    @GET("/api/v4/coupons")
    public void getCoupons(@Query("token") String token, @Query("lat") String lat, @Query("long") String lng, Callback<BaseResponse<WalletData>> callback);

    @GET("/api/v4/locations")
    public void getLocations(Callback<BaseResponse<ArrayList<LocationData>>> callback);

    @POST("/api/v4/locations/verify")
    public void postLocationsVerify(@Query("token") String token, @Body() LocationsVerify locationsVerify, Callback<BaseResponse<ArrayList<LocationsVerified>>> callback);

    @POST("/api/v4/outlet/suggestion")
    public void postSuggestion(@Query("token") String token, @Body() Suggestion suggestion, Callback<BaseResponse> callback);

    @POST("/api/v4/offer/submit")
    public void postSubmitOffer(@Query("token") String token, @Body() SubmitOffer submitOffer, Callback<BaseResponse> callback);

    @POST("/api/v4/checkin/qr")
    public void postCheckin(@Query("token") String token, @Body() CheckinData checkinData, Callback<BaseResponse> callback);

    @POST("/api/v4/offer/like")
    public void postLikeOffer(@Query("token") String token, @Body() LikeOffer likeOffer, Callback<BaseResponse<Data>> callback);

    @POST("/api/v4/offer/unlike")
    public void postUnLikeOffer(@Query("token") String token, @Body() LikeOffer likeOffer, Callback<BaseResponse> callback);

    @POST("/api/v4/outlet/share")
    public void shareOutlet(@Query("token") String token, @Body() ShareOutlet shareOutlet, Callback<BaseResponse> callback);

    @POST("/api/v4/offer/share")
    public void shareOffer(@Query("token") String token, @Body() ShareOffer shareOffer, Callback<BaseResponse> callback);

    @POST("/api/v4/offer/extend")
    public void extendVoucher(@Query("token") String token, @Body() Voucher voucher, Callback<BaseResponse> callback);

    @POST("/api/v4/coupon/grab")
    public void grabOffer(@Query("token") String token, @Body() GrabOffer grabOffer, Callback<BaseResponse> callback);

    @POST("/api/v4/checkin/bill")
    public void uploadBill(@Query("token") String token, @Body() UploadBill uploadBill, Callback<BaseResponse> callback);

    @POST("/api/v4/comments")
    public void writeToUs(@Query("token") String token, @Body() WriteToUs writeToUs, Callback<BaseResponse> callback);

    @POST("/api/v4/deal/log")
    public void postDealLog(@Query("token") String token, @Body() UseOffer useOffer, Callback<BaseResponse> callback);

    @POST("/api/v4/offer/generate/coupon")
    public void postGenerateCoupon(@Query("token") String token, @Body() UseOffer useOffer, Callback<BaseResponse> callback);

    @POST("/api/v4/coupon/redeem")
    public void postRedeemCoupon(@Query("token") String token, @Body() UseOffer useOffer, Callback<BaseResponse> callback);

    @POST("/api/v4/referral/join")
    public void postReferral(@Query("token") String token, @Body() Referral referral, Callback<BaseResponse> callback);

    @POST("/api/v4/offer/report/problem")
    public void reportProblem(@Query("token") String token, @Body() ReportProblem reportProblem, Callback<BaseResponse> callback);

    @GET("/api/v4/profile")
    public void getProfile(@Query("token") String token, Callback<BaseResponse<Profile>> callback);

    @GET("/api/v4/search")
    public void searchOffer(@Query("token") String token, @Query("text") String searchText, @Query("lat") String lat, @Query("long") String lng, @Query("date") String date, @Query(value = "time", encodeValue = false) String time, Callback<BaseResponse<DiscoverData>> callback);

    @POST("/api/v4/user/location")
    public void postLocation(@Query("token") String token, @Body() UserLocation userLocation, Callback<BaseResponse> callback);

    @GET("/api/v4/events/notifications")
    public void getNotification(@Query("token") String token, Callback<BaseResponse<ArrayList<NotificationData>>> callback);

    @GET("/api/v4/offers/outlet/{outlet_id}")
    public void getOffers(@Path("outlet_id") String outletID, @Query("token") String token, Callback<BaseResponse<ArrayList<Offer>>> callback);

    @GET("/api/v4/offers")
    public void getFoodOffers(@Query("token") String token, Callback<BaseResponse<ArrayList<FoodOffer>>> callback);

    @PUT("/api/v4/order")
    public void putOrderUpdate(@Query("token") String token, @Body() OrderUpdate orderUpdate, Callback<BaseResponse> callback);

    @GET("/api/v4/cashback/offers")
    public void getCashbackOffers(@Query("token") String token, Callback<BaseResponse<ArrayList<Cashback>>> callback);

}
