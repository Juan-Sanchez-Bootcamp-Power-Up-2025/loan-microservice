package co.com.crediya.loan.api;

import co.com.crediya.loan.api.dto.LoanApplicationRequestDto;
import co.com.crediya.loan.model.loanapplication.LoanApplication;
import co.com.crediya.loan.model.loanapplication.LoanApplicationReview;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.util.List;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/loans",
                    method = RequestMethod.POST,
                    produces = MediaType.APPLICATION_JSON_VALUE,
                    beanClass = Handler.class,
                    beanMethod = "listenSaveLoan",
                    operation = @Operation(
                            operationId = "listenSaveLoan",
                            summary = "Register new loan",
                            description = "Creates a new loan with amount, term, email, status, type, document id",
                            requestBody = @RequestBody(
                                    required = true,
                                    content = @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(implementation = LoanApplicationRequestDto.class),
                                            examples = {
                                                    @ExampleObject(name = "Loan example",
                                                            value = """
                                                                    {
                                                                        "email": "name@crediya.com",
                                                                        "documentId": "12345678",
                                                                        "type": "LOW",
                                                                        "amount": 12345,
                                                                        "term": 10
                                                                    }
                                                                    """,
                                                            description = "Loan example to test the creation of a loan."
                                                    )
                                            }
                                    )
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200", description = "Loan created",
                                            content = @Content(schema = @Schema(implementation = LoanApplication.class),
                                                    examples = {
                                                            @ExampleObject(name = "Loan example",
                                                                    value = """
                                                                    {
                                                                        "email": "name@crediya.com",
                                                                        "documentId": "12345678",
                                                                        "status": "PENDING",
                                                                        "type": "LOW",
                                                                        "amount": 12345,
                                                                        "term": 10,
                                                                        "monthlyDebt": 123543
                                                                    }
                                                                    """,
                                                                    description = "Loan example to test the creation of a loan."
                                                            )
                                                    }
                                            )
                                    ),
                                    @ApiResponse(
                                            responseCode = "400", description = "Invalid Data",
                                            content = @Content(schema = @Schema(implementation = List.class),
                                                    examples = {@ExampleObject(
                                                            name = "Mandatory fields",
                                                            value = """
                                                            {
                                                                 "error": "Invalid data",
                                                                  "violations": [
                                                                      {
                                                                          "message": "term must be greater than 1",
                                                                          "field": "term"
                                                                      },
                                                                      {
                                                                          "message": "amount must be greater than 0",
                                                                          "field": "amount"
                                                                      },
                                                                      {
                                                                          "message": "email format is not valid",
                                                                          "field": "email"
                                                                      }
                                                                  ]
                                                            }
                                                            """,
                                                            description = "Bad request for invalid fields."
                                                    ), @ExampleObject(
                                                            name = "Invalid type",
                                                            value = """
                                                                    {
                                                                         "error": "Loan Type error",
                                                                         "violations": "Loan type HIGH4 not found in data base. Please check the type."
                                                                     }
                                                            """,
                                                            description = "Bad request for type."
                                                    ), @ExampleObject(
                                                            name = "Invalid user parameters",
                                                            value = """
                                                                    {
                                                                         "error": "User error",
                                                                         "violations": "User with email = email@crediya.com and document id = 12345678 was not found."
                                                                     }
                                                            """,
                                                            description = "Bad request for user parameters."
                                                    )}
                                            )
                                    ),
                                    @ApiResponse(responseCode = "500", description = "Internal Error")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/loans",
                    method = RequestMethod.GET,
                    produces = MediaType.APPLICATION_JSON_VALUE,
                    beanClass = Handler.class,
                    beanMethod = "listenGetLoansListPaginate",
                    operation = @Operation(
                            operationId = "listenGetLoansListPaginate",
                            summary = "Gets loans",
                            description = "Gets loans with status MANUAL_REVISION, PENDING or REJECTED",
                            parameters = {
                                    @Parameter(
                                            name = "page",
                                            example = "2"
                                    ),
                                    @Parameter(
                                            name = "size",
                                            example = "4"
                                    )
                            },
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200", description = "Loan list",
                                            content = @Content(
                                                    examples = {
                                                            @ExampleObject(name = "Loan list paginated example",
                                                                    value = """
                                                                            {
                                                                                 "loanApplicationReviews": [
                                                                                     {
                                                                                         "email": "client@crediya.com",
                                                                                         "documentId": "12345678",
                                                                                         "status": "PENDING",
                                                                                         "type": "LOW",
                                                                                         "amount": 327875,
                                                                                         "term": 2,
                                                                                         "monthlyDebt": 116899
                                                                                     },
                                                                                     {
                                                                                         "email": "client@crediya.com",
                                                                                         "documentId": "12345678",
                                                                                         "status": "PENDING",
                                                                                         "type": "LOW",
                                                                                         "amount": 78645,
                                                                                         "term": 78,
                                                                                         "monthlyDebt": 116899
                                                                                     },
                                                                                     {
                                                                                         "email": "client@crediya.com",
                                                                                         "documentId": "12345678",
                                                                                         "status": "REJECTED",
                                                                                         "type": "HIGH",
                                                                                         "amount": 124,
                                                                                         "term": 4,
                                                                                         "monthlyDebt": 116899
                                                                                     }
                                                                                 ],
                                                                                 "total": 7,
                                                                                 "page": 2,
                                                                                 "size": 4
                                                                             }
                                                                    """,
                                                                    description = "Loan list example with page and size parameters."
                                                            ),
                                                            @ExampleObject(name = "Loan list example",
                                                                    value = """
                                                                             [
                                                                                 {
                                                                                     "email": "client@crediya.com",
                                                                                     "documentId": "12345678",
                                                                                     "status": "PENDING",
                                                                                     "type": "LOW",
                                                                                     "amount": 327875,
                                                                                     "term": 2,
                                                                                     "monthlyDebt": 116899
                                                                                 },
                                                                                 {
                                                                                     "email": "client@crediya.com",
                                                                                     "documentId": "12345678",
                                                                                     "status": "PENDING",
                                                                                     "type": "LOW",
                                                                                     "amount": 78645,
                                                                                     "term": 78,
                                                                                     "monthlyDebt": 116899
                                                                                 },
                                                                                 {
                                                                                     "email": "client@crediya.com",
                                                                                     "documentId": "12345678",
                                                                                     "status": "REJECTED",
                                                                                     "type": "HIGH",
                                                                                     "amount": 124,
                                                                                     "term": 4,
                                                                                     "monthlyDebt": 116899
                                                                                 }
                                                                             ]
                                                                    """,
                                                                    description = "Loan list example with no parameters."
                                                            )
                                                    }
                                            )
                                    ),
                                    @ApiResponse(responseCode = "500", description = "Internal Error")
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route(POST("/api/v1/loans"), handler::listenSaveLoan)
                .andRoute(GET("api/v1/loans"), handler::listenGetLoansList);
    }

}
