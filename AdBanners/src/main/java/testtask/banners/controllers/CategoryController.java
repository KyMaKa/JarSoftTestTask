package testtask.banners.controllers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.mediatype.problem.Problem;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
import testtask.banners.data.modelAssemblers.CategoryModelAssembler;
import testtask.banners.data.models.Category;
import testtask.banners.service.CategoryService;

@RestController
@RequestMapping(path = "/categories")
public class CategoryController {

  private final CategoryService categoryService;

  private final CategoryModelAssembler assembler;

  @Autowired
  public CategoryController(CategoryService categoryService,
      CategoryModelAssembler assembler) {
    this.categoryService = categoryService;
    this.assembler = assembler;
  }

  /**
   * Creates new category entity and adds it to DB.
   * @param category - entity to be created.
   * @return - created entity, Bad Request if name is empty, Conflict if category
   * with given name already exists.
   */
  @PostMapping(path = "/add")
  public ResponseEntity<?> addNewCategory (@RequestBody Category category) {
    if (category.getName().isEmpty())
      return ResponseEntity
          .status(HttpStatus.BAD_REQUEST)
          .header(HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE)
          .body(Problem.create()
              .withTitle("Bad request")
              .withDetail("Category name can't be empty."));

    if (categoryService.getCategory(category.getName()) == null) {
      EntityModel<Category> createdCategory = assembler.toModel(
          categoryService.createCategory(category));
      return ResponseEntity
          .created(createdCategory.getRequiredLink(IanaLinkRelations.SELF).toUri())
          .body(createdCategory);
    }

    return ResponseEntity
        .status(HttpStatus.CONFLICT)
        .header(HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE)
        .body(Problem.create()
            .withTitle("Conflict")
            .withDetail("Category with name " + category.getName() + " already exist."));
  }

  /**
   * Gets a list of all not deleted categories.
   * @return a list of categories.
   */
  @GetMapping(path = "/all")
  public @ResponseBody
  CollectionModel<EntityModel<Category>> getAllCategory() {
    List<Category> categories = categoryService.getAllCategory();
    List<EntityModel<Category>> c = categories.stream().map(assembler::toModel).toList();
    return CollectionModel.of(c,
        linkTo(methodOn(CategoryController.class).getAllCategory()).withSelfRel());
  }

  /**
   * Gets a category with given id. Not used in client side.
   * @param id - of category to return.
   * @return - not deleted category.
   */
  @GetMapping(path = "/{id}")
  public @ResponseBody EntityModel<Category> getCategory(@PathVariable("id") Long id) {
    Category category = categoryService.getCategory(id);

    return assembler.toModel(category);
  }

  /**
   * updates category - renames it.
   * @param newCategory - updated category.
   * @param id - of category to update.
   * @return - updated category if renames successfully, Bad Request if name is empty, Conflict
   * if category with new name already exists.
   */
  @PutMapping("/{id}")
  public ResponseEntity<?> renameCategory(@RequestBody Category newCategory, @PathVariable("id") Long id) {
    if (newCategory.getName().isEmpty())
      return ResponseEntity
          .status(HttpStatus.BAD_REQUEST)
          .header(HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE)
          .body(Problem.create()
              .withTitle("Bad request")
              .withDetail("Category name can't be empty."));

    if (categoryService.getCategory(newCategory.getName()) == null || Objects.equals(
        categoryService.getCategory(newCategory.getName()).getId(), id)) {
      Category updatedCategory = categoryService.updateCategory(newCategory, id);
      EntityModel<Category> entityModel = assembler.toModel(updatedCategory);
      return ResponseEntity
          .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
          .body(entityModel);
    }
    return ResponseEntity
        .status(HttpStatus.CONFLICT)
        .header(HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE)
        .body(Problem.create()
            .withTitle("Conflict")
            .withDetail("Category with name " + newCategory.getName() + " already exist."));
  }

  /**
   * Mark category as deleted in DB.
   * @param id - of category to delete.
   * @return - no content or Method not Allowed if category contains banners.
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteCategory(@PathVariable("id") Long id) {
    Category category = categoryService.getCategory(id);
    if (category.getBanners().isEmpty()) {
      categoryService.deleteCategory(id);
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity
        .status(HttpStatus.METHOD_NOT_ALLOWED)
        .header(HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE)
        .body(Problem.create()
            .withTitle("Method not allowed")
            .withDetail("You can't delete an category while it contains banners "
            + category.getBanners()));
  }
}
