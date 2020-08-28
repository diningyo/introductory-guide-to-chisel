module BlackBoxDelay
    (
        input logic clk,
        input logic rst_n,
        input logic [7:0] in,
        output logic [7:0] out
    );


    always_ff @(posedge clk or negedge rst_n) begin
        if (!rst_n) begin
            out <= `DELAY 'h0;
        end
        else begin
            out <= in;
        end

    end

endmodule : BlackBoxDelay
