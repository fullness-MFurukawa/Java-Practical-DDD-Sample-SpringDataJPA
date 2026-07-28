package jp.co.fullness.ddd.presentation.product.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import jp.co.fullness.ddd.application.dto.CategoryDTO;
import jp.co.fullness.ddd.application.dto.ProductDTO;
import jp.co.fullness.ddd.application.dto.StockDTO;
import jp.co.fullness.ddd.application.product.usecase.UpdateProductUsecase;
import jp.co.fullness.ddd.presentation.product.schema.ProductUpdateSchemaMapper;

/**
 * {@link UpdateProductController} のHTTP層テスト（@WebMvcTest）。
 *
 * <p>Usecase / Mapper をモック化し、取得（GET）・更新（PUT）のルーティング・
 * 入力検証・レスポンス整形を検証する。例外→ステータス（404/409、パス外の400）は
 * ApiExceptionHandler が必要になるため、本テストではハンドラ無しで成立する範囲のみを検証する。</p>
 */
@WebMvcTest(UpdateProductController.class)
@DisplayName("UpdateProductController: 商品変更エンドポイント")
class UpdateProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UpdateProductUsecase usecase;
    @MockitoBean
    private ProductUpdateSchemaMapper mapper;

    @Test
    @DisplayName("GET /api/products/{id}: 見つかれば 200 と商品を返す")
    void getProduct_found() throws Exception {
        var dto = new ProductDTO("pid", "蛍光ペン", 130,
                new CategoryDTO("cid", "文房具"), new StockDTO("sid", 10));
        when(usecase.getProduct("pid")).thenReturn(dto);

        mockMvc.perform(get("/api/products/{id}", "pid"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("pid"))
                .andExpect(jsonPath("$.name").value("蛍光ペン"))
                .andExpect(jsonPath("$.category.name").value("文房具"))
                .andExpect(jsonPath("$.stock.quantity").value(10));
    }

    @Test
    @DisplayName("PUT /api/products/{id}: 正常なら 200・本文を返し、パスのIDがDTOへ補完される")
    void update_valid() throws Exception {
        // mapper が返す DTO（id 未設定・category null）
        var mapped = new ProductDTO(null, "新商品", 750, null, new StockDTO(null, 42));
        // usecase が返す変更後 DTO
        var updated = new ProductDTO("pid", "新商品", 750,
                new CategoryDTO("cid", "文房具"), new StockDTO("sid", 42));
        when(mapper.toDto(any())).thenReturn(mapped);
        when(usecase.updateProduct(any())).thenReturn(updated);

        String body = """
                {"name":"新商品","price":750,"stockQuantity":42}
                """;

        mockMvc.perform(put("/api/products/{id}", "pid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("pid"))
                .andExpect(jsonPath("$.name").value("新商品"))
                .andExpect(jsonPath("$.stock.quantity").value(42));

        // パスの {id} が DTO に補完されて usecase に渡ること
        ArgumentCaptor<ProductDTO> captor = ArgumentCaptor.forClass(ProductDTO.class);
        verify(usecase).updateProduct(captor.capture());
        assertEquals("pid", captor.getValue().getId());
    }

    @Test
    @DisplayName("PUT /api/products/{id}: 商品名が空なら 400（@Valid によるボディ検証）")
    void update_invalidBody() throws Exception {
        String body = """
                {"name":"","price":750,"stockQuantity":42}
                """;

        mockMvc.perform(put("/api/products/{id}", "pid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }
}