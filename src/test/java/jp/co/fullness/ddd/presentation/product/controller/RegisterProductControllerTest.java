package jp.co.fullness.ddd.presentation.product.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import jp.co.fullness.ddd.application.dto.CategoryDTO;
import jp.co.fullness.ddd.application.dto.ProductDTO;
import jp.co.fullness.ddd.application.dto.StockDTO;
import jp.co.fullness.ddd.application.product.usecase.RegisterProductUsecase;
import jp.co.fullness.ddd.presentation.product.schema.ProductCreateSchemaMapper;

/**
 * {@link RegisterProductController} のHTTP層テスト（@WebMvcTest）。
 *
 * <p>Usecase / Mapper をモック化し、ルーティング・入力検証・レスポンス整形を検証する。
 * 例外→ステータス（404/409、パラメータ制約違反の400）は ApiExceptionHandler が
 * 必要になるため、本テストではハンドラ無しで成立する範囲のみを検証する。</p>
 */
@WebMvcTest(RegisterProductController.class)
@DisplayName("RegisterProductController: 商品登録エンドポイント")
class RegisterProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RegisterProductUsecase usecase;
    @MockitoBean
    private ProductCreateSchemaMapper mapper;

    @Test
    @DisplayName("GET /categories: 200 とカテゴリ一覧を返す")
    void getCategories() throws Exception {
        when(usecase.getCategories()).thenReturn(List.of(new CategoryDTO("cid", "文房具")));

        mockMvc.perform(get("/api/products/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("cid"))
                .andExpect(jsonPath("$[0].name").value("文房具"));
    }

    @Test
    @DisplayName("GET /categories/{id}: 200 とカテゴリを返す")
    void getCategoryById() throws Exception {
        when(usecase.getCategoryById("cid")).thenReturn(new CategoryDTO("cid", "文房具"));

        mockMvc.perform(get("/api/products/categories/{id}", "cid"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("cid"))
                .andExpect(jsonPath("$.name").value("文房具"));
    }

    @Test
    @DisplayName("GET /exists: 存在しなければ 204（usecase へ委譲）")
    void checkExists_notExists() throws Exception {
        // usecase.existsProduct は例外を投げない（＝存在しない）
        mockMvc.perform(get("/api/products/exists").param("name", "万年筆"))
                .andExpect(status().isNoContent());

        verify(usecase).existsProduct("万年筆");
    }

    @Test
    @DisplayName("POST /api/products: 正常なら 201・Location・本文を返す")
    void register_valid() throws Exception {
        var mapped = new ProductDTO(null, "筆ペン", 300,
                new CategoryDTO("cid", "文房具"), new StockDTO(null, 10));
        var created = new ProductDTO("83fbc81d-2498-4da6-b8c2-54878d3b67ff", "筆ペン", 300,
                new CategoryDTO("cid", "文房具"), new StockDTO("sid", 10));
        when(mapper.toDto(any())).thenReturn(mapped);
        when(usecase.addProduct(any())).thenReturn(created);

        String body = """
                {"name":"筆ペン","price":300,"categoryId":"2d8e2b0d-49ef-4b36-a4f3-1c6a2e0b84c4","stockQuantity":10}
                """;

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/products/83fbc81d-2498-4da6-b8c2-54878d3b67ff"))
                .andExpect(jsonPath("$.id").value("83fbc81d-2498-4da6-b8c2-54878d3b67ff"))
                .andExpect(jsonPath("$.name").value("筆ペン"));

        verify(usecase).addProduct(any());
    }

    @Test
    @DisplayName("POST /api/products: 商品名が空なら 400（@Valid によるボディ検証）")
    void register_invalidBody() throws Exception {
        String body = """
                {"name":"","price":300,"categoryId":"2d8e2b0d-49ef-4b36-a4f3-1c6a2e0b84c4","stockQuantity":10}
                """;

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }
}