package com.dperez.CarRegistry.controller.mapper;

import com.dperez.CarRegistry.controller.dtos.BrandDTO;
import com.dperez.CarRegistry.controller.dtos.BrandDTO.BrandDTOBuilder;
import com.dperez.CarRegistry.controller.dtos.CarDTOAndBrand;
import com.dperez.CarRegistry.controller.dtos.CarDTOAndBrand.CarDTOAndBrandBuilder;
import com.dperez.CarRegistry.service.model.Brand;
import com.dperez.CarRegistry.service.model.Brand.BrandBuilder;
import com.dperez.CarRegistry.service.model.Car;
import com.dperez.CarRegistry.service.model.Car.CarBuilder;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-07-07T18:44:03+0200",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 17.0.8 (Oracle Corporation)"
)
@Component
public class CarDTOAndBrandMapperImpl implements CarDTOAndBrandMapper {

    @Override
    public CarDTOAndBrand carToCarDTOAndBrand(Car car) {
        if ( car == null ) {
            return null;
        }

        CarDTOAndBrandBuilder carDTOAndBrand = CarDTOAndBrand.builder();

        carDTOAndBrand.id( car.getId() );
        carDTOAndBrand.brand( brandToBrandDTO( car.getBrand() ) );
        carDTOAndBrand.model( car.getModel() );
        carDTOAndBrand.mileage( car.getMileage() );
        carDTOAndBrand.price( car.getPrice() );
        carDTOAndBrand.year( car.getYear() );
        carDTOAndBrand.description( car.getDescription() );
        carDTOAndBrand.color( car.getColor() );
        carDTOAndBrand.fuelType( car.getFuelType() );
        carDTOAndBrand.numDoors( car.getNumDoors() );

        return carDTOAndBrand.build();
    }

    @Override
    public Car carDTOAndBrandToCar(CarDTOAndBrand carDTOAndBrand) {
        if ( carDTOAndBrand == null ) {
            return null;
        }

        CarBuilder car = Car.builder();

        car.id( carDTOAndBrand.getId() );
        car.brand( brandDTOToBrand( carDTOAndBrand.getBrand() ) );
        car.model( carDTOAndBrand.getModel() );
        car.mileage( carDTOAndBrand.getMileage() );
        car.price( carDTOAndBrand.getPrice() );
        car.year( carDTOAndBrand.getYear() );
        car.description( carDTOAndBrand.getDescription() );
        car.color( carDTOAndBrand.getColor() );
        car.fuelType( carDTOAndBrand.getFuelType() );
        car.numDoors( carDTOAndBrand.getNumDoors() );

        return car.build();
    }

    protected BrandDTO brandToBrandDTO(Brand brand) {
        if ( brand == null ) {
            return null;
        }

        BrandDTOBuilder brandDTO = BrandDTO.builder();

        brandDTO.id( brand.getId() );
        brandDTO.name( brand.getName() );
        brandDTO.warranty( brand.getWarranty() );
        brandDTO.country( brand.getCountry() );

        return brandDTO.build();
    }

    protected Brand brandDTOToBrand(BrandDTO brandDTO) {
        if ( brandDTO == null ) {
            return null;
        }

        BrandBuilder brand = Brand.builder();

        brand.id( brandDTO.getId() );
        brand.name( brandDTO.getName() );
        brand.warranty( brandDTO.getWarranty() );
        brand.country( brandDTO.getCountry() );

        return brand.build();
    }
}
