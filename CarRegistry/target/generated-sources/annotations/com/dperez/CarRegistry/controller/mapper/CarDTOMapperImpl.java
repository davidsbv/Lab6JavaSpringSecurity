package com.dperez.CarRegistry.controller.mapper;

import com.dperez.CarRegistry.controller.dtos.CarDTO;
import com.dperez.CarRegistry.controller.dtos.CarDTO.CarDTOBuilder;
import com.dperez.CarRegistry.service.model.Brand;
import com.dperez.CarRegistry.service.model.Brand.BrandBuilder;
import com.dperez.CarRegistry.service.model.Car;
import com.dperez.CarRegistry.service.model.Car.CarBuilder;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-07-07T18:44:02+0200",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 17.0.8 (Oracle Corporation)"
)
@Component
public class CarDTOMapperImpl implements CarDTOMapper {

    @Override
    public CarDTO carToCarDTO(Car car) {
        if ( car == null ) {
            return null;
        }

        CarDTOBuilder carDTO = CarDTO.builder();

        carDTO.brand( carBrandName( car ) );
        carDTO.id( car.getId() );
        carDTO.model( car.getModel() );
        carDTO.mileage( car.getMileage() );
        carDTO.price( car.getPrice() );
        carDTO.year( car.getYear() );
        carDTO.description( car.getDescription() );
        carDTO.color( car.getColor() );
        carDTO.fuelType( car.getFuelType() );
        carDTO.numDoors( car.getNumDoors() );

        return carDTO.build();
    }

    @Override
    public Car carDTOToCar(CarDTO carDTO) {
        if ( carDTO == null ) {
            return null;
        }

        CarBuilder car = Car.builder();

        car.brand( carDTOToBrand( carDTO ) );
        car.id( carDTO.getId() );
        car.model( carDTO.getModel() );
        car.mileage( carDTO.getMileage() );
        car.price( carDTO.getPrice() );
        car.year( carDTO.getYear() );
        car.description( carDTO.getDescription() );
        car.color( carDTO.getColor() );
        car.fuelType( carDTO.getFuelType() );
        car.numDoors( carDTO.getNumDoors() );

        return car.build();
    }

    private String carBrandName(Car car) {
        if ( car == null ) {
            return null;
        }
        Brand brand = car.getBrand();
        if ( brand == null ) {
            return null;
        }
        String name = brand.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }

    protected Brand carDTOToBrand(CarDTO carDTO) {
        if ( carDTO == null ) {
            return null;
        }

        BrandBuilder brand = Brand.builder();

        brand.name( carDTO.getBrand() );

        return brand.build();
    }
}
