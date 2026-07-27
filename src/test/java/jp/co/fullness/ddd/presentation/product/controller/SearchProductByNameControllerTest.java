package jp.co.fullness.ddd.presentation.product.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import jp.co.fullness.ddd.application.dto.CategoryDTO;
import jp.co.fullness.ddd.application.dto.ProductDTO;
import jp.co.fullness.ddd.application.dto.StockDTO;
import jp.co.fullness.ddd.application.product.usecase.SearchProductByNameUsecase;

/**
 * {@link SearchProductByNameController} のHTTP層テスト（@WebMvcTest）。
 *
 * <p>Usecase をモック化し、正常系と必須パラメータ欠落（400）を検証する。
 * 404（NotFoundException）や空文字パラメータの400は ApiExceptionHandler が必要なため、
 * ハンドラ作成後に追加する。</p>
 */
@WebMvcTest(SearchProductByNameController.class)
@DisplayName("SearchProductByNameController: 商品名検索エンドポイント")
class SearchProductByNameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SearchProductByNameUsecase usecase;

    @Test
    @DisplayName("GET /search: 見つかれば 200 と商品を返す")
    void search_found() throws Exception {
        var dto = new ProductDTO("pid", "蛍光ペン", 130,
                new CategoryDTO("cid", "文房具"), new StockDTO("sid", 10));
        when(usecase.search(any())).thenReturn(dto);

        mockMvc.perform(get("/api/products/search").param("name", "蛍光ペン"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("pid"))
                .andExpect(jsonPath("$.name").value("蛍光ペン"))
                .andExpect(jsonPath("$.category.name").value("文房具"))
                .andExpect(jsonPath("$.stock.quantity").value(10));
    }

    @Test
    @DisplayName("GET /search: name パラメータ欠落なら 400")
    void search_missingParam() throws Exception {
        mockMvc.perform(get("/api/products/search"))
                .andExpect(status().isBadRequest());
    }
}