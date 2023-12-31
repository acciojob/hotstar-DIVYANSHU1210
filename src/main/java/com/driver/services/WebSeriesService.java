package com.driver.services;

import com.driver.EntryDto.WebSeriesEntryDto;
import com.driver.model.ProductionHouse;
import com.driver.model.WebSeries;
import com.driver.repository.ProductionHouseRepository;
import com.driver.repository.WebSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WebSeriesService {

    @Autowired
    WebSeriesRepository webSeriesRepository;

    @Autowired
    ProductionHouseRepository productionHouseRepository;

    public Integer addWebSeries(WebSeriesEntryDto webSeriesEntryDto)throws  Exception{

        //Add a webSeries to the database and update the ratings of the productionHouse
        //Incase the seriesName is already present in the Db throw Exception("Series is already present")
        //use function written in Repository Layer for the same
        //Dont forget to save the production and webseries Repo


        String seriesName = webSeriesEntryDto.getSeriesName();

        if(webSeriesRepository.findBySeriesName(seriesName)== null){
            ProductionHouse productionHouse = productionHouseRepository.findById(webSeriesEntryDto.getProductionHouseId()).get();
            WebSeries webSeries = new WebSeries();
            webSeries.setSeriesName(seriesName);
            webSeries.setAgeLimit(webSeriesEntryDto.getAgeLimit());
            webSeries.setRating(webSeriesEntryDto.getRating());
            webSeries.setSubscriptionType(webSeriesEntryDto.getSubscriptionType());
            webSeries.setProductionHouse(productionHouse);

            Double productionHouseRating = (productionHouse.getRatings() + webSeries.getRating())/2;
            productionHouse.setRatings(productionHouseRating);



            webSeries = webSeriesRepository.save(webSeries);

            productionHouse.getWebSeriesList().add(webSeries);

            productionHouseRepository.save(productionHouse);

            return webSeries.getId();
        }
        else{
            throw new Exception("Series is already present");
        }




    }

}
