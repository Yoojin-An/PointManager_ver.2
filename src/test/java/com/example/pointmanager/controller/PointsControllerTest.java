package com.example.pointmanager.controller;

import com.example.pointmanager.domain.Points;
import com.example.pointmanager.domain.PointsHistory;
import com.example.pointmanager.exception.InvalidPointsException;
import com.example.pointmanager.repository.PointsHistoryRepository;
import com.example.pointmanager.repository.PointsRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import static com.example.pointmanager.domain.PointsHistory.TransactionType.USE;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;


@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
public class PointsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PointsRepository pointsRepository;

    @Autowired
    private PointsHistoryRepository pointsHistoryRepository;

    @Test
    void 헬스체크_결과가_200으로_정상이다() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk());

    }

    @Test
    void 클라이언트는_특정_유저의_포인트_현황을_전달받을_수_있다() throws Exception {
        pointsRepository.save(new Points(1, 10000));

        mockMvc.perform(get("/points/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(10000));
    }

    @Test
    void 클라이언트는_사용_또는_충전_이력이_없는_유저의_포인트_현황을_전달받을_수_없다() throws Exception {
        mockMvc.perform(get("/points/{id}", 1))
                .andExpect(status().isNotFound());
    }

    @Test
    void 클라이언트는_특정_유저의_포인트_충전_또는_사용_이력을_전달받을_수_있다() throws Exception {
        pointsHistoryRepository.save(PointsHistory.of(1, 10000, USE));
        mockMvc.perform(get("/points/{id}/history", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].amount").value(10000))
                .andExpect(jsonPath("$[0].transactionType").value("USE"));
    }

    @Test
    void 클라이언트는_사용_이력이_없는_유저의_사용_이력을_전달받을_수_없다() throws Exception {
        mockMvc.perform(get("/points/{id}/history", 1))
                .andExpect(status().isNotExtended())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof InvalidPointsException));
    }

    @Test
    void 클라이언트는_특정_유저의_포인트를_충전할_수_있다() throws Exception {
        pointsRepository.save(new Points(1, 0)); // TODO: 최초 유저는 서비스에서 new로 Points 만들어주는데 이 코드가 없으면 왜 안 되지?

        Map<String, Integer> input = new HashMap<>();
        input.put("amount", 10000);

        mockMvc.perform(patch("/points/{id}/charge", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(10000));
    }

    @Test
    void 클라이언트는_특정_유저의_포인트를_사용할_수_있다() throws Exception {
        pointsRepository.save(new Points(1, 10000));

        Map<String, Integer> input = new HashMap<>();
        input.put("amount", 3000);

        mockMvc.perform(patch("/points/{id}/use", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(7000));
    }

    @Test
    void 클라이언트는_존재하지_않는_유저의_포인트를_사용할_수_없다() throws Exception {
        Map<String, Integer> input = new HashMap<>();
        input.put("amount", 10000);

        mockMvc.perform(patch("/points/{id}/use", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest());
    }
}
