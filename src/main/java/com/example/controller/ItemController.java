package com.example.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.entity.Category;
import com.example.entity.Item;
import com.example.form.ItemForm;
import com.example.service.CategoryService;
import com.example.service.ItemService;

@Controller
@RequestMapping("/item")
public class ItemController {
	
	private final ItemService itemService;
	
	private final CategoryService categoryService;

    @Autowired
    public ItemController(ItemService itemService, CategoryService categoryService) {
        this.itemService = itemService;
        this.categoryService = categoryService;
    }

	// 商品一覧の表示
    @GetMapping
    public String index(Model model) {
    	List<Item> items = this.itemService.findByDeletedAtIsNull();
    	model.addAttribute("items", items);
        return "item/index";
    }

    // 商品登録ページ表示用
    @GetMapping("toroku")
    public String torokuPage(@ModelAttribute("itemForm") ItemForm itemForm, Model model) {
    	List<Category> categories = this.categoryService.findAll();
    	model.addAttribute("categories", categories);
    	return "item/torokuPage";
    }

    // 商品登録の実行
    @PostMapping("toroku")
    public String toroku(ItemForm itemForm) {
    	this.itemService.save(itemForm);
        return "redirect:/item";
    }

    // 商品編集ページ
    @GetMapping("henshu/{id}")
    public String henshuPage(@PathVariable("id") Integer id, Model model
                             , @ModelAttribute("itemForm") ItemForm itemForm) {
        Item item = this.itemService.findById(id);
        // フィールドのセット
        itemForm.setName(item.getName());
        itemForm.setPrice(item.getPrice());
     // カテゴリIDをformにセット
        itemForm.setCategoryId(item.getCategoryId());

        // Categoryモデルから一覧を取得
        List<Category> categories = this.categoryService.findAll();
        // idをセット
        model.addAttribute("id", id);
     // viewにカテゴリを渡す
        model.addAttribute("categories", categories);
        return "item/henshuPage";
    }

    // 商品編集の実行
    @PostMapping("henshu/{id}")
    public String henshu(@PathVariable("id") Integer id, @ModelAttribute("itemForm") ItemForm itemForm) {
    	this.itemService.update(id, itemForm);
        return "redirect:/item";
    }

    // 商品削除の実行
    @PostMapping("sakujo/{id}")
    public String sakujo(@PathVariable("id") Integer id) {
    	this.itemService.delete(id);
        return "redirect:/item";
    }
    
 // 送信ボタンのname属性が in の場合は入荷処理の実行
    @PostMapping(path = "stock/{id}", params = "in")
    public String nyuka(@PathVariable("id") Integer id, @RequestParam("stock") Integer inputValue) {
    	// 入荷処理
        this.itemService.nyuka(id, inputValue);
        return "redirect:/item";
    }

    // 送信ボタンのname属性が out の場合は出荷処理の実行
    @PostMapping(path = "stock/{id}", params = "out")
    public String shukka(@PathVariable("id") Integer id, @RequestParam("stock") Integer inputValue) {
    	// 出荷処理
        this.itemService.shukka(id, inputValue);
        return "redirect:/item";
    }

}
