package com.dperez.CarRegistry.controller;

import com.dperez.CarRegistry.controller.dtos.CarDTO;
import com.dperez.CarRegistry.controller.dtos.CarDTOAndBrand;
import com.dperez.CarRegistry.controller.mapper.CarDTOAndBrandMapper;
import com.dperez.CarRegistry.controller.mapper.CarDTOMapper;
import com.dperez.CarRegistry.service.CarService;
import com.dperez.CarRegistry.service.model.Car;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/cars")
public class CarController {

    @Autowired
    private CarService carService;

    @PostMapping("add-car")
    @PreAuthorize("hasRole('VENDOR')")
    public ResponseEntity<?> addCar(@RequestBody CarDTO carDTO){

        try {
            // Se convierte carDTO a Car y se utiliza en la llmada al método addCar.
            // Cuando se guarda se devuelve en newCarDTO  y se muestra la respuesta
            Car car = CarDTOMapper.INSTANCE.carDTOToCar(carDTO);
            Car newCar = carService.addCar(car);
            CarDTOAndBrand newCarDTOAndBrand = CarDTOAndBrandMapper.INSTANCE.carToCarDTOAndBrand(newCar);
            log.info("New Car added");
            return ResponseEntity.ok(newCarDTOAndBrand);

        } catch (IllegalArgumentException e) {
            // Error por Id ya existente.
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());

        } catch (Exception e){
            log.error("Error while adding new car");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Añadir lista de coches
    @PostMapping("add-bunch")
    @PreAuthorize("hasRole('VENDOR')")
    public CompletableFuture<ResponseEntity<?>> addBunchCars(@RequestBody List<CarDTO> carDTOs){

        try {
            // Convierte la lista de CarDTO a Car y llama al método addCars del servicio
            // Después pasa cada objeto car del stream un objeto CarDTOAndBrand y lo devuelve
            List<Car> cars = carDTOs.stream().map(CarDTOMapper.INSTANCE::carDTOToCar).collect(Collectors.toList());
            return carService.addBunchCars(cars).thenApply(addedCars -> {
                List<CarDTOAndBrand> addedCarDTOsAndBrand = addedCars.stream()
                        .map(CarDTOAndBrandMapper.INSTANCE::carToCarDTOAndBrand).toList();

                log.info("Adding several cars");
                return ResponseEntity.ok(addedCarDTOsAndBrand);
            });
        }  catch (IllegalArgumentException e) {
            // Error en la id o marca
            log.error(e.getMessage());
            return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()));

        } catch (Exception e){
            log.error("Error while adding new car");
            return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
        }
    }

    @GetMapping("get-car/{id}")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<?> getCarById(@PathVariable Integer id){

        // Se busca la id solicitada. Si existe se devuelve la información del coche y la marca.
        // Si no devuelve mensaje de error.
        Car car = carService.getCarById(id);
        if (car != null){
            log.info("Car info loaded");
            CarDTOAndBrand carDTOAndBrand = CarDTOAndBrandMapper.INSTANCE.carToCarDTOAndBrand(car);
            return ResponseEntity.ok(carDTOAndBrand);
        }
        else {
            log.error("Id does not exist");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Car not found");
        }
    }

    @PutMapping("update-car/{id}")
    @PreAuthorize("hasRole('VENDOR')")
    public ResponseEntity<?> updateCarById(@PathVariable Integer id, @RequestBody CarDTO carDto){

        try {
            // Mapear carDTO a Car y llamada al método updateCarById
            Car car = CarDTOMapper.INSTANCE.carDTOToCar(carDto);
            Car carToUpdate = carService.updateCarById(id, car);

            // Mapear Car a CarDTO y devolver CarDTO actualizado
            CarDTOAndBrand carUpdated = CarDTOAndBrandMapper.INSTANCE.carToCarDTOAndBrand(carToUpdate);
            log.info("Car updated");
            return ResponseEntity.ok(carUpdated);

        } catch (IllegalArgumentException e) {  // Error en la id pasada
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

        } catch (Exception e){
            log.error("Error while updating car");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    // Actualizar lista de coches
    @PutMapping("update-bunch")
    @PreAuthorize("hasRole('VENDOR')")
    public CompletableFuture<ResponseEntity<?>> updateBunch(@RequestBody List<CarDTO> carDTOs){

        try {
            List<Car> cars = carDTOs.stream().map(CarDTOMapper.INSTANCE::carDTOToCar).toList();
            return carService.updateBunchCars(cars).thenApply(updatedCars -> {
                List<CarDTOAndBrand> updatedCarDTOAndBrand = updatedCars.stream()
                        .map(CarDTOAndBrandMapper.INSTANCE::carToCarDTOAndBrand).toList();

                log.info("Updating several cars");
                return ResponseEntity.ok(updatedCarDTOAndBrand);
            });
        }catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()));

        } catch (Exception e){
            log.error("Error while updating car");
            return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
        }
    }

    @DeleteMapping("delete-car/{id}")
    @PreAuthorize("hasRole('VENDOR')")
    public ResponseEntity<?> deleteCarById(@PathVariable Integer id){

        try {
            carService.deleteCarById(id);
            return ResponseEntity.status(HttpStatus.OK).body("Deleted Car with Id: " + id);

        } catch (IllegalArgumentException e) { // Error en la id pasada
           return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

        } catch (Exception e){
            log.error("Deleting car error");
            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Recuperar la información de todos los coches
    @GetMapping("get-all")
    @PreAuthorize("hasRole('CLIENT')")
    public CompletableFuture<ResponseEntity<?>> getAllCars(){

        // Mapea la lista con objetos Car a una lista con objetos carDTOAndBrand y muestra su resultado.
        CompletableFuture<List<Car>> cars =  carService.getAllCars();
        return cars.thenApply(carRecovered -> {
            List<CarDTOAndBrand> carDTOsAndBrand = carRecovered.stream()
                .map(CarDTOAndBrandMapper.INSTANCE::carToCarDTOAndBrand).toList();

            log.info("Rcovering all cars");
            return ResponseEntity.ok(carDTOsAndBrand);
        });
    }
}
