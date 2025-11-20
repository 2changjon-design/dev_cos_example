package com.example.study.lecture.refactorspringsection1answer;

import com.example.study.lecture.refactorspringsection1.RefactorSection1OrderLine;
import com.example.study.lecture.refactorspringsection1.RefactorSection1OrderRequest;
import org.mapstruct.Mapper;

/**
 * MapStruct 기반 Request -> Domain Mapper.
 */
@Mapper(componentModel = "spring")
public interface RefactorSection1OrderMapperAfterAnswer {

    RefactorSection1OrderLine toOrderLine(RefactorSection1OrderRequest.OrderLineRequest request);
}
