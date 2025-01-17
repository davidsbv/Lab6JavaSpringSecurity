package com.dperez.CarRegistry.controller.mapper;

import com.dperez.CarRegistry.controller.dtos.BrandDTO;
import com.dperez.CarRegistry.controller.dtos.BrandDTO.BrandDTOBuilder;
import com.dperez.CarRegistry.service.model.Brand;
import com.dperez.CarRegistry.service.model.Brand.BrandBuilder;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-07-07T18:44:03+0200",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 17.0.8 (Oracle Corporation)"
)
@Component
public class BrandDTOMapperImpl implements BrandDTOMapper {

    @Override
    public BrandDTO brandToBrandDTO(Brand brand) {
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

    @Override
    public Brand brandDTOToBrand(BrandDTO brandDTO) {
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
