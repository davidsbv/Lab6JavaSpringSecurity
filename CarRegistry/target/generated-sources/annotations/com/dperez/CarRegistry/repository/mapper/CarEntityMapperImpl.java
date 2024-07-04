package com.dperez.CarRegistry.repository.mapper;

import com.dperez.CarRegistry.repository.entity.BrandEntity;
import com.dperez.CarRegistry.repository.entity.BrandEntity.BrandEntityBuilder;
import com.dperez.CarRegistry.repository.entity.CarEntity;
import com.dperez.CarRegistry.repository.entity.CarEntity.CarEntityBuilder;
import com.dperez.CarRegistry.service.model.Brand;
import com.dperez.CarRegistry.service.model.Brand.BrandBuilder;
import com.dperez.CarRegistry.service.model.Car;
import com.dperez.CarRegistry.service.model.Car.CarBuilder;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-07-01T19:25:11+0200",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 17.0.8 (Oracle Corporation)"
)
@Component
public class CarEntityMapperImpl implements CarEntityMapper {

    @Override
    public Car carEntityToCar(CarEntity carEntity) {
        if ( carEntity == null ) {
            return null;
        }

        CarBuilder car = Car.builder();

        car.id( carEntity.getId() );
        car.brand( brandEntityToBrand( carEntity.getBrand() ) );
        car.model( carEntity.getModel() );
        car.mileage( carEntity.getMileage() );
        car.price( carEntity.getPrice() );
        car.year( carEntity.getYear() );
        car.description( carEntity.getDescription() );
        car.color( carEntity.getColor() );
        car.fuelType( carEntity.getFuelType() );
        car.numDoors( carEntity.getNumDoors() );

        return car.build();
    }

    @Override
    public CarEntity carToCarEntity(Car car) {
        if ( car == null ) {
            return null;
        }

        CarEntityBuilder carEntity = CarEntity.builder();

        carEntity.id( car.getId() );
        carEntity.brand( brandToBrandEntity( car.getBrand() ) );
        carEntity.model( car.getModel() );
        carEntity.mileage( car.getMileage() );
        carEntity.price( car.getPrice() );
        carEntity.year( car.getYear() );
        carEntity.description( car.getDescription() );
        carEntity.color( car.getColor() );
        carEntity.fuelType( car.getFuelType() );
        carEntity.numDoors( car.getNumDoors() );

        return carEntity.build();
    }

    protected Brand brandEntityToBrand(BrandEntity brandEntity) {
        if ( brandEntity == null ) {
            return null;
        }

        BrandBuilder brand = Brand.builder();

        brand.id( brandEntity.getId() );
        brand.name( brandEntity.getName() );
        brand.warranty( brandEntity.getWarranty() );
        brand.country( brandEntity.getCountry() );

        return brand.build();
    }

    protected BrandEntity brandToBrandEntity(Brand brand) {
        if ( brand == null ) {
            return null;
        }

        BrandEntityBuilder brandEntity = BrandEntity.builder();

        brandEntity.id( brand.getId() );
        brandEntity.name( brand.getName() );
        brandEntity.warranty( brand.getWarranty() );
        brandEntity.country( brand.getCountry() );

        return brandEntity.build();
    }
}
