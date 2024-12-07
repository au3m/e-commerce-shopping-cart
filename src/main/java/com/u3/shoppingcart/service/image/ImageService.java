package com.u3.shoppingcart.service.image;

import com.u3.shoppingcart.dto.ImageDto;
import com.u3.shoppingcart.exceptions.ResourceNOtFoundException;
import com.u3.shoppingcart.model.Image;
import com.u3.shoppingcart.model.Product;
import com.u3.shoppingcart.repository.ImageRepository;
import com.u3.shoppingcart.service.product.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.lang.module.ResolutionException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ImageService implements IImageService {
    private final ImageRepository imageRepository;
    private final IProductService productService;

    public ImageService(ImageRepository imageRepository, IProductService productService) {
        this.imageRepository = imageRepository;
        this.productService = productService;
    }

    @Override
    public Image getImageById(Long id) {
        return imageRepository.findById(id)
                .orElseThrow(() -> new ResolutionException("NO image found with id: " + id));
    }

    @Override
    public void deleteImageById(Long id) {
        imageRepository.findById(id)
                .ifPresentOrElse(imageRepository::delete, () -> {
                    throw new ResourceNOtFoundException("No image found with id: " + id);
                });
    }

    @Override
    public List<ImageDto> saveImages(List<MultipartFile> files, Long productId) {
        Product product = productService.getProductById(productId);
        List<ImageDto> savedImageDto = new ArrayList<>();
        for (MultipartFile multipartFile : files) {
            try{
                Image image = new Image();
                image.setFilename(multipartFile.getOriginalFilename());
                image.setFiletype(multipartFile.getContentType());
                image.setImage(new SerialBlob(multipartFile.getBytes()));
                image.setProduct(product);

                String downloadUrl = "/api/v1/images/image/download/" + image.getId();
                image.setDownloadUrl(downloadUrl);
                Image savedImage = imageRepository.save(image);
                savedImage.setDownloadUrl("/api/v1/images/image/download/" + savedImage.getId());
                imageRepository.save(savedImage);

                ImageDto imageDto = new ImageDto();
                imageDto.setId(savedImage.getId());
                imageDto.setFilename(savedImage.getFilename());
                imageDto.setDownloadUrl(savedImage.getDownloadUrl());
                savedImageDto.add(imageDto);



            }   catch (IOException | SQLException e) {
                throw new RuntimeException(e.getMessage());
            }
        }
        return savedImageDto;
    }

    @Override
    public void updateImage(MultipartFile file, Long imageId) {
        try {
            Image image = getImageById(imageId);
            image.setFilename(file.getOriginalFilename());
            image.setFiletype(file.getContentType());
            image.setImage(new SerialBlob(file.getBytes()));
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
