//package com.ExclusiveService.util;
//
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//public class CalculatorTest {
//    private Calculator calculator;
//    @BeforeEach
//    void setUp() {
//        calculator = new Calculator();
//    }
//    @Test
//    void testSum(){
//        Assertions.assertEquals(10, calculator.sum(5, 5));
//        Assertions.assertEquals(14, calculator.sum(5, 9));
//        Assertions.assertEquals(8, calculator.sum(6, 2));
//        Assertions.assertEquals(11, calculator.sum(8, 3));
//    }
//    @Test
//    void testMultiply(){
//        Assertions.assertEquals(10, calculator.sum(5, 2));
//        Assertions.assertEquals(14, calculator.sum(2, 7));
//        Assertions.assertEquals(16, calculator.sum(4, 4));
//        Assertions.assertEquals(24, calculator.sum(8, 3));
//    }@Test
//    void testExtract(){
//        Assertions.assertEquals(10, calculator.sum(15, 5));
//        Assertions.assertEquals(16, calculator.sum(25, 9));
//        Assertions.assertEquals(8, calculator.sum(16, 8));
//        Assertions.assertEquals(11, calculator.sum(14, 3));
//    }
//}
