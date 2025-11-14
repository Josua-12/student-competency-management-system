package com.competency.scms.dto.competency;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class CompetencyTreeDto {

    // TUI-Tree가 요구하는 필드 1: 노드 ID
    private Long id;
    // TUI-Tree가 요구하는 필드 2: 노드 텍스트
    private String text;
    // TUI-Tree가 요구하는 필드 3: 하위 노드 (재귀)
    private List<CompetencyTreeDto> children;

    // (TUI-Tree의 'state' 옵션 - 'opened'/'closed' - 을 설정하기 위한 필드)
    private State state;

    // DB를 안전하게 담을 보관함
    private Map<String, Object> data;

    @Getter
    @Builder
    public static class State { // 👈 TUI-Tree의 state 옵션용 내부 DTO
        private boolean opened;
        // private boolean selected;
    }

    @Builder
    public CompetencyTreeDto(Long id, String text, List<CompetencyTreeDto> children, boolean opened, Map<String, Object> data) {
        this.id = id;
        this.text = text;
        this.children = children;
        // 빌더 파라미터(boolean)로 내부 DTO(State)를 생성해서 할당
        this.state = State.builder().opened(opened).build();
        this.data = data;
    }
}
