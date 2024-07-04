package com.dperez.CarRegistry.service.impl;

import com.dperez.CarRegistry.repository.BrandRepository;
import com.dperez.CarRegistry.repository.CarRepository;
import com.dperez.CarRegistry.repository.entity.BrandEntity;
import com.dperez.CarRegistry.repository.entity.CarEntity;
import com.dperez.CarRegistry.repository.mapper.BrandEntityMapper;
import com.dperez.CarRegistry.repository.mapper.CarEntityMapper;
import com.dperez.CarRegistry.service.CarService;
import com.dperez.CarRegistry.service.model.Brand;
import com.dperez.CarRegistry.service.model.Car;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service
 public class CarServiceImpl implements CarService {

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private CarEntityMapper carEntityMapper;

    @Override
    public Car addCar(Car car) throws IllegalArgumentException {

        // Verifica si la Id ya existe. Lanza una excepción en caso afirmativo.
        if(car.getId() != null && carRepository.existsById(car.getId())){

            throw new IllegalArgumentException("The Id already" + car.getId() +" exists");
        }

        // Verificar si la marca existe
        Optional<BrandEntity> brandEntityOptional = brandRepository.findByNameIgnoreCase(car.getBrand().getName());

        if(brandEntityOptional.isEmpty()){
            throw new IllegalArgumentException("Brand " + car.getBrand().getName() + " does not exist");
        }

        // Obtener la BrandEntity existente y pasar a Brand
        BrandEntity brandEntity = brandEntityOptional.get();
        Brand brand = BrandEntityMapper.INSTANCE.brandEntityToBrand(brandEntity);

        // Asociar la BrandEntitiy existente al car
        car.setBrand(brand);

        // Se pasa car a carEntity para guardar
        CarEntity carEntity = CarEntityMapper.INSTANCE.carToCarEntity(car);
       // carEntity.setBrand((brand)); // Se asocia la brand existente

        // Se guarda la CarEntity en la base de datos
        CarEntity savedCarEntity = carRepository.save(carEntity);

        // Se devuelve el coche guardado como modelo de dominio
        return CarEntityMapper.INSTANCE.carEntityToCar(savedCarEntity);
    }
    @Async
    @Override
    public List<Car> addBunchCarsV2(List<Car> cars) {
            long startTime = System.currentTimeMillis();

        // Obtener la lista de marcas antes de entrar al bucle
        List<BrandEntity> brandList = brandRepository.findAll();
        Map<String, BrandEntity> brandMap = brandList.stream()
                .collect(Collectors.toMap(brand -> brand.getName().toLowerCase(), brand -> brand));

        // Obrener todos los coches
        List<CarEntity> carsInDB = carRepository.findAll();


        // Verificar si la lista está vacía con brandList.size() o Objects.isEmpty() y lanzar error
        if (brandList.isEmpty()){
            throw new IllegalArgumentException("No brands found");
        }
        List<CarEntity> carEntitiesToAdd = cars.stream().map(car -> {
            // Comprobar si la id ya existe
            if ((car.getId() != null) && carsInDB.stream()
                    .map(CarEntity::getId).collect(Collectors.toSet()).contains(car.getId())) {
                throw new IllegalArgumentException("The id" + car.getId() + " already exists.");
            }
            BrandEntity brandEntity = brandMap.get(car.getBrand().getName().toLowerCase());
            if (brandEntity == null) {
                throw new IllegalArgumentException("Brand " + car.getBrand().getName() + " does not exist");
            }
            // Convertir a carEntity y asignar la marca
            CarEntity carEntityToSave = carEntityMapper.carToCarEntity(car);
            carEntityToSave.setBrand(brandEntity);
            return carEntityToSave;
        }).toList();
        // Se guardan los coches en una sola oparación
        List<CarEntity> savedCarEntities = carRepository.saveAll(carEntitiesToAdd);

        // Se pasan los carEntity a Car y se devuelven
//        Licst<CarEntity> carEntitiesToSave = carsToAdd.stream().map(carEntityMapper::carToCarEntity).toList();
       List<Car> savedCars = savedCarEntities.stream().map(carEntity -> carEntityMapper.carEntityToCar(carEntity)).toList();
        return (List<Car>) CompletableFuture.completedFuture(savedCars);
    }

    @Async("taskExecutor")
    @Override
    public CompletableFuture<List<Car>> addBunchCars(List<Car> cars) throws IllegalArgumentException {
        long starTime = System.currentTimeMillis();

        // Verificar si la id existe y si la marca está en la base de datos
        List<Car> addedCars = cars.stream().map(car -> {
            if ((car.getId() != null) && carRepository.existsById(car.getId())){
               throw new IllegalArgumentException("The Id " + car.getId() + " already exists");
            }
            // Se obtienen las marcas de los coches a actualizar
            Optional<BrandEntity> brandEntityOptional = brandRepository.findByNameIgnoreCase(car.getBrand().getName());

            if (brandEntityOptional.isEmpty()){
                throw new IllegalArgumentException("Brand " + car.getBrand().getName() + " does not exist");
            }

            // Toma la BrandEntity, se le asigna a cada CarEntity que se ha transformado previamente de Car
            BrandEntity brandEntity = brandEntityOptional.get();
            CarEntity carEntity = CarEntityMapper.INSTANCE.carToCarEntity(car);
            carEntity.setBrand(brandEntity);

            // Se guarda el CarEntity con los datos validados, en savedCar para retornarlos al stream como Car
            CarEntity savedCarEntity = carRepository.save(carEntity);

            return CarEntityMapper.INSTANCE.carEntityToCar(savedCarEntity);
        }).toList();

        long endTime = System.currentTimeMillis();
        log.info("Total time: " + (endTime-starTime) + "ms");

        // Se devulven los coches guardados
        return CompletableFuture.completedFuture(addedCars);
    }


    @Override
    public Car getCarById(Integer id) {

        // Búsqueda de car por id
        Optional<CarEntity> carEntityOptional = carRepository.findById(id);

        log.info("Searching id: " + id);
        // Si se encuentra devuelve el objeto car. En caso contrario devuelve null.
        return carEntityOptional.map(CarEntityMapper.INSTANCE::carEntityToCar).orElse(null);
    }


    @Override
    public Car updateCarById(Integer id, Car car) throws IllegalArgumentException {

        // Verifica si la Marca del objeto Car existe
        Optional<BrandEntity> brandEntityOptional = brandRepository.findByNameIgnoreCase(car.getBrand().getName());

        if (brandEntityOptional.isEmpty()){
            log.error("Unknown id");
            throw new IllegalArgumentException("Brand with name " + car.getBrand() + " does not exist.");
        }

        // Verifica si la Id existe. Lanza excepción en caso negativo. En caso afirmativo actualiza los datos
        if(id == null || !carRepository.existsById(id)){
            log.error("Unknown id");
           throw new IllegalArgumentException("Id " + id + " does not exist.");
        }
        else {
            // Se obtiene la BrandEntity existente y se asocia a la carEntity a actualizar
            BrandEntity brandEntity = brandEntityOptional.get();
            log.info("Marca " + brandEntity.toString() + " encontrada");
            CarEntity carEntity = CarEntityMapper.INSTANCE.carToCarEntity(car);

            // Seteo de la id y la marca
            carEntity.setId(id);
            carEntity.setBrand(brandEntity);

            // Actualiza los datos y devuelve el objeto actualizado.
            CarEntity updatedCarEntity = carRepository.save(carEntity);
            return CarEntityMapper.INSTANCE.carEntityToCar(updatedCarEntity);
        }

    }

    @Async("taskExecutor")
    @Override
    public CompletableFuture<List<Car>> updateBunchCars(List<Car> cars) throws IllegalArgumentException {
        long starTime = System.currentTimeMillis();

        // Se guardan los Car actualizados haciendo las comprobaciones de marca e id existentes.
        List<Car> updatedCars = cars.stream().map(car -> {

            // Pasar de Brand a BrandEntity y comprobar que está dada de alta en la base de datos
            BrandEntity brandEntity = BrandEntityMapper.INSTANCE.brandToBrandEntity(car.getBrand());
            Optional<BrandEntity> brandEntityOptional = brandRepository.findByNameIgnoreCase(brandEntity.getName());

            // Comprobación de Brand
            if (brandEntityOptional.isEmpty()){
                throw new IllegalArgumentException("Brand: " + brandEntity.getName() + " not yet registred");
            }
            // Comprobación de id
            if ((car.getId() == null) || (!carRepository.existsById(car.getId()))){
                throw new IllegalArgumentException("Id: " + car.getId() + " does not exist");
            }

            // Si existen id y brand de cada car se toman los datos de Brand de la base de datos
            brandEntity = brandEntityOptional.get();

            // Pasamos los Car a CarEntity
            CarEntity carEntity = CarEntityMapper.INSTANCE.carToCarEntity(car);

            // Se setea BrandEntity para su CarEntity
            carEntity.setBrand(brandEntity);

            // Se actualizan los datos
            CarEntity updatedCarEntity = carRepository.save(carEntity);

            // Se devuelve el Car actualizado
            return CarEntityMapper.INSTANCE.carEntityToCar(updatedCarEntity);
        }).toList();

        long endTime = System.currentTimeMillis();
        log.info("Total time: " + (endTime-starTime) + "ms");

        return CompletableFuture.completedFuture(updatedCars);
    }


    @Override
    public void deleteCarById(Integer id) throws IllegalArgumentException {

        // Si la id existe borra el coche. En caso contrario lanza error.
        if(id != null && carRepository.existsById(id)){
            carRepository.deleteById(id);
        }
        else {
            throw new IllegalArgumentException("Car not found with Id: " + id);
        }
    }

    @Async("taskExecutor")
    @Override
    public CompletableFuture<List<Car>> getAllCars() {
        long starTime = System.currentTimeMillis();

        // Se obtienen en una lista todos los objetos de tipo CarEntity y se mapean a tipo Car
        //MODIFICADO CarEntityMapper.INSTANCE::carEntityToCar POR carEntityMapper::carEntityToCar
        // mejora la velocidad porque no tiene que cargar toda la clase por debajo
        List<Car> allCars = carRepository.findAll().stream()
                .map(carEntityMapper::carEntityToCar)
                .toList();

        long endTime = System.currentTimeMillis();
        log.info("Total time: " + (endTime-starTime) + "ms");

        // Se devuelve el resultado
        return CompletableFuture.completedFuture(allCars);
    }
}
