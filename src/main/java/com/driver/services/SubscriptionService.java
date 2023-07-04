package com.driver.services;


import com.driver.EntryDto.SubscriptionEntryDto;
import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.repository.SubscriptionRepository;
import com.driver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Service
public class SubscriptionService {

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    UserRepository userRepository;

    public Integer buySubscription(SubscriptionEntryDto subscriptionEntryDto){

        //Save The subscription Object into the Db and return the total Amount that user has to pay


        User user = userRepository.findById(subscriptionEntryDto.getUserId()).get();
        SubscriptionType subscriptionType = subscriptionEntryDto.getSubscriptionType();
        int noOfScreens = subscriptionEntryDto.getNoOfScreensRequired();

        int price = 0;
        if(subscriptionType == SubscriptionType.BASIC){
            price = 500 + (200 * noOfScreens);
        } else if (subscriptionType == SubscriptionType.PRO) {
            price = 800 + (250 * noOfScreens);
        }else if(subscriptionType == SubscriptionType.ELITE){
            price = 1000 + (350 * noOfScreens);
        }

        Subscription subscription = new Subscription();
        subscription.setSubscriptionType(subscriptionType);
        subscription.setNoOfScreensSubscribed(noOfScreens);
        subscription.setTotalAmountPaid(price);
        subscription.setStartSubscriptionDate(new Date());       /////////////////////////////////////////////////////// DOUBT

        subscription.setUser(user);
        user.setSubscription(subscription);

        userRepository.save(user);

        return price;
    }

    public Integer upgradeSubscription(Integer userId)throws Exception{

        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        //update the subscription in the repository

        User user = userRepository.findById(userId).get();
        Subscription subscription = user.getSubscription();

        if(subscription.getSubscriptionType() == SubscriptionType.ELITE){
            throw new Exception("Already the best Subscription");
        }
        int noOfScreensSubscribed = subscription.getNoOfScreensSubscribed();
        int oldPrice = subscription.getTotalAmountPaid();
        int newPrice = 0;


//        agar hum basic me hai to pro me , agar pro pe hai to elite pe upgrade     ///////////////////////////////
        if(subscription.getSubscriptionType() == SubscriptionType.BASIC){
            subscription.setSubscriptionType(SubscriptionType.PRO);
            newPrice = 800 + 250*noOfScreensSubscribed;
        }
        else{
            subscription.setSubscriptionType(SubscriptionType.ELITE);
            newPrice = 1000 + 350*noOfScreensSubscribed;
        }

        subscription.setTotalAmountPaid(newPrice);
        int amountToBePaid = oldPrice - newPrice;

        user.setSubscription(subscription);

        userRepository.save(user);
        return amountToBePaid;
    }

    public Integer calculateTotalRevenueOfHotstar(){

        //We need to find out total Revenue of hotstar : from all the subscriptions combined
        //Hint is to use findAll function from the SubscriptionDb

        List<Subscription> subscriptionList = subscriptionRepository.findAll();
        int revenue = 0;

        for(Subscription subscription : subscriptionList){
            revenue += subscription.getTotalAmountPaid();
        }

        return revenue;
    }

}
