package testtask.banners.web;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import testtask.banners.data.modelAssemblers.BannerModelAssembler;
import testtask.banners.data.models.Banner;
import testtask.banners.data.models.Category;
import testtask.banners.service.BannerService;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping(path = "/banners")
public class BannerController {

  private final BannerService bannerService;
  private final BannerModelAssembler assembler;

  @Autowired
  public BannerController(BannerService bannerService,
      BannerModelAssembler assembler) {
    this.bannerService = bannerService;
    this.assembler = assembler;
  }

  @GetMapping(path = "/{id}")
  public @ResponseBody
  EntityModel<Banner> getBanner(@PathVariable(name = "id") Long id) {
    Banner banner = bannerService.getBanner(id);
    return assembler.toModel(banner);
  }

  @GetMapping(path = "/all")
  public @ResponseBody
  CollectionModel<EntityModel<Banner>> getAllBanner() {
    List<Banner> banners = bannerService.getAllBanner();
    List<EntityModel<Banner>> b = banners.stream().map(assembler::toModel).toList();
    return CollectionModel.of(b,
        linkTo(methodOn(BannerController.class).getAllBanner()).withSelfRel());
  }

  @PostMapping()
  public ResponseEntity<?> addBanner(Banner banner) {
    EntityModel<Banner> entityModel = assembler.toModel(bannerService.createBanner(banner));

    return ResponseEntity
        .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
        .body(entityModel);
  }

  @PutMapping("/{id}")
  public ResponseEntity<?> updateBanner(@RequestBody Banner newBanner, @PathVariable("id") Long id) {
    Banner updatedBanner = bannerService.updateBanner(newBanner, id);
    EntityModel<Banner> entityModel = assembler.toModel(updatedBanner);
    return ResponseEntity
        .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
        .body(entityModel);
  }

  @PutMapping(path = "/add/{id}")
  public ResponseEntity<?> addCategoryToBanner(@RequestBody Category category, @PathVariable("id") Long id) {
    Banner banner = bannerService.addCategory(category, bannerService.getBanner(id));
    EntityModel<Banner> entityModel = assembler.toModel(banner);
    return ResponseEntity
        .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
        .body(entityModel);
  }


  @DeleteMapping(path = "{id}")
  public ResponseEntity<?> deleteBanner(@PathVariable(name = "id") Long id) {
    bannerService.deleteBanner(id);

    return ResponseEntity.noContent().build();
  }

}