/**
 * Enum class representing all supported instructions and their
 * respective operation codes.
 */
enum class Instruction (val opcode: String){
    ADD_DOUBLE("add-double"),
    ADD_DOUBLE_2ADDR("add-double/2addr"),
    ADD_FLOAT("add-float"),
    ADD_FLOAT_2ADDR("add-float/2addr"),
    ADD_INT("add-int"),
    ADD_INT_2ADDR("add-int/2addr"),
    ADD_INT_LIT16("add-int/lit16"),
    ADD_INT_LIT8("add-int/lit8"),
    ADD_LONG("add-long"),
    ADD_LONG_2ADDR("add-long/2addr"),
    AGET("aget"),
    AGET_BOOLEAN("aget-wide"),
    AGET_BYTE("aget-byte"),
    AGET_CHAR("aget-char"),
    AGET_OBJECT("aget-object"),
    AGET_SHORT("aget-short"),
    AGET_WIDE("aget-wide"),
    AND_INT("and-int"),
    AND_INT_2ADDR("and-int/2addr"),
    AND_INT_LIT16("and-int/lit16"),
    AND_INT_LIT8("and-int/lit8"),
    AND_LONG("and-long"),
    AND_LONG_2ADDR("and-long/2addr"),
    APUT("aput"),
    APUT_BOOLEAN("aput-boolean"),
    APUT_BYTE("aput-byte"),
    APUT_CHAR("aput-char"),
    APUT_OBJECT("aput-object"),
    APUT_SHORT("aput-short"),
    APUT_WIDE("aput-wide"),
    ARRAY_LENGTH("array-length"),
    BREAKPOINT("breakpoint"),
    CHECK_CAST("check-cast"),
    CMPG_DOUBLE("cmpg-double"),
    CMPG_FLOAT("cmpg-float"),
    CMPL_DOUBLE("cmpl-double"),
    CMPL_FLOAT("cmpl-float"),
    CMP_LONG("cmp-long"),
    CONST("const"),
    CONST_16("const/16"),
    CONST_4("const/4"),
    CONST_CLASS("const-class"),
    CONST_HIGH16("const/high16"),
    CONST_STRING("const-string"),
    CONST_WIDE("const-wide"),
    CONST_WIDE_16("const-wide/16"),
    CONST_WIDE_32("const-wide/32"),
    CONST_WIDE_HIGH16("const-wide/high16"),
    DIV_DOUBLE("div-double"),
    DIV_DOUBLE_2ADDR("div-double/2addr"),
    DIV_FLOAT("div-float"),
    DIV_FLOAT_2ADDR("div-float/2addr"),
    DIV_INT("div-int"),
    DIV_INT_2ADDR("div-int/2addr"),
    DIV_INT_LIT16("div-int/lit16"),
    DIV_INT_LIT8("div-int/lit8"),
    DIV_LONG("div-long"),
    DIV_LONG_2ADDR("div-long/2addr"),
    DOUBLE_TO_FLOAT("double-to-float"),
    DOUBLE_TO_INT("double-to-int"),
    DOUBLE_TO_LONG("double-to-long"),
    FILLED_NEW_ARRAY("filled-new-array"),
    FILLED_NEW_ARRAY_RANGE("filled-new-array/range"),
    FILL_ARRAY_DATA("fill-array-data"),
    FLOAT_TO_DOUBLE("float-to-double"),
    FLOAT_TO_INT("float-to-int"),
    FLOAT_TO_LONG("float-to-long"),
    GOTO("goto"),
    GOTO_16("goto/16"),
    GOTO_32("goto/32"),
    IF_EQ("if-eq"),
    IF_EQZ("if-eqz"),
    IF_GE("if-ge"),
    IF_GEZ("if-gez"),
    IF_GT("if-gt"),
    IF_GTZ("if-gtz"),
    IF_LE("if-le"),
    IF_LEZ("if-lez"),
    IF_LT("if-lt"),
    IF_LTZ("if-ltz"),
    IF_NE("if-ne"),
    IF_NEZ("if-nez"),
    IGET("iget"),
    IGET_BOOLEAN("iget-boolean"),
    IGET_BYTE("iget-byte"),
    IGET_CHAR("iget-char"),
    IGET_OBJECT("iget-object"),
    IGET_OBJECT_QUICK("iget-object/quick"),
    IGET_QUICK("iget-quick"),
    IGET_SHORT("iget-short"),
    IGET_WIDE("iget-wide"),
    IGET_WIDE_QUICK("iget-wide/quick"),
    IGET_WIDE_VOLATILE("iget-wide/volatile"),
    INSTANCE_OF("instance_of"),
    INT_TO_BYTE("int-to-byte"),
    INT_TO_CHAR("int-to-char"),
    INT_TO_DOUBLE("int-to-double"),
    INT_TO_FLOAT("int-to-float"),
    INT_TO_LONG("int-to-long"),
    INT_TO_SHORT("int-to-short"),
    INVOKE_CUSTOM("invoke"),
    INVOKE_CUSTOM_RANGE("invoke-custom/range"),
    INVOKE_DIRECT("invoke-direct"),
    INVOKE_DIRECT_EMPTY("invoke-direct/empty"),
    INVOKE_DIRECT_RANGE("invoke-direct/range"),
    INVOKE_INTERFACE("invoke-interface"),
    INVOKE_INTERFACE_RANGE("invoke-interface/range"),
    INVOKE_POLYMORPHIC("invoke-polymorphic"),
    INVOKE_POLYMORPHIC_RANGE("invoke-polymorphic/range"),
    INVOKE_STATIC("invoke-static"),
    INVOKE_STATIC_RANGE("invoke-static/range"),
    INVOKE_SUPER("invoke-super"),
    INVOKE_SUPER_RANGE("invoke-super/range"),
    INVOKE_VIRTUAL("invoke-virtual"),
    INVOKE_VIRTUAL_RANGE("invoke-virtual/range"),
    IPUT("iput"),
    IPUT_BOOLEAN("iput-boolean"),
    IPUT_BYTE("iput-byte"),
    IPUT_CHAR("iput-char"),
    IPUT_OBJECT("iput-object"),
    IPUT_SHORT("iput-short"),
    IPUT_WIDE("iput-wide"),
    LONG_TO_DOUBLE("long-to-double"),
    LONG_TO_FLOAT("long-to-float"),
    LONG_TO_INT("long-to-int"),
    MONITOR_ENTER("monitor-enter"),
    MONITOR_EXIT("monitor-exit"),
    MOVE("move"),
    MOVE_16("move/16"),
    MOVE_EXCEPTION("move-exception"),
    MOVE_FROM16("move/from16"),
    MOVE_OBJECT("move-object"),
    MOVE_OBJECT_16("move-object/16"),
    MOVE_OBJECT_FROM16("move-object/from16"),
    MOVE_RESULT("move-result"),
    MOVE_RESULT_OBJECT("move-result-object"),
    MOVE_RESULT_WIDE("move-result-wide"),
    MOVE_WIDE("move-wide"),
    MOVE_WIDE_16("move-wide/16"),
    MOVE_WIDE_FROM16("move-wide/from16"),
    MUL_DOUBLE("mul-double"),
    MUL_DOUBLE_2ADDR("mul-double/2addr"),
    MUL_FLOAT("mul-float"),
    MUL_FLOAT_2ADDR("mul-float/2addr"),
    MUL_INT("mul-int"),
    MUL_INT_2ADDR("mul-int/2addr"),
    MUL_INT_LIT16("mul-int/lit16"),
    MUL_INT_LIT8("mul-int/lit8"),
    MUL_LONG("mul-long"),
    MUL_LONG_2ADDR("mul-long/2addr"),
    NEG_DOUBLE("neg-double"),
    NEG_FLOAT("neg-float"),
    NEG_INT("neg-int"),
    NEG_LONG("neg-long"),
    NEW_ARRAY("new-array"),
    NEW_ARRAY_JUMBO("new-array/jumbo"),
    NEW_INSTANCE("new-instance"),
    NEW_INSTANCE_JUMBO("new-instance/jumbo"),
    NOP("nop"),
    NOT_INT("not-int"),
    NOT_LONG("not-long"),
    OR_INT("or-int"),
    OR_INT_2ADDR("or-int/2addr"),
    OR_INT_LIT16("or-int/lit16"),
    OR_INT_LIT8("or-int/lit8"),
    OR_LONG("or-long"),
    OR_LONG_2ADDR("or-long/2addr"),
    PACKED_SWITCH("packed-switch"),
    REM_DOUBLE("rem-double"),
    REM_DOUBLE_2ADDR("rem-double/2addr"),
    REM_FLOAT("rem-float"),
    REM_FLOAT_2ADDR("rem-float/2addr"),
    REM_INT("rem-int"),
    REM_INT_2ADDR("rem-int/2addr"),
    REM_INT_LIT16("rem-int/lit16"),
    REM_INT_LIT8("rem-int/lit8"),
    REM_LONG("rem-long"),
    REM_LONG_2ADDR("rem-long/2addr"),
    RETURN("return"),
    RETURN_OBJECT("return-object"),
    RETURN_VOID("return-void"),
    RETURN_WIDE("return-wide"),
    RSUB_INT("rsub-int"),
    RSUB_INT_LIT8("rsub-int/lit8"),
    SGET("sget"),
    SGET_BOOLEAN("sget-boolean"),
    SGET_BYTE("sget-byte"),
    SGET_CHAR("sget-char"),
    SGET_OBJECT("sget-object"),
    SGET_SHORT("sget-short"),
    SGET_WIDE("sget-wide"),
    SHL_INT("shl-int"),
    SHL_INT_2ADDR("shl-int/2addr"),
    SHL_INT_LIT8("shl-int/lit8"),
    SHL_LONG("shl-long"),
    SHL_LONG_2ADDR("shl-long/2addr"),
    SHR_INT("shr-int"),
    SHR_INT_2ADDR("shr-int/2addr"),
    SHR_INT_LIT8("shr-int/lit8"),
    SHR_LONG("shr-long"),
    SHR_LONG_2ADDR("shr-long/2addr"),
    SPARSE_SWITCH("sparse-switch"),
    SPUT("sput"),
    SPUT_BOOLEAN("sput-boolean"),
    SPUT_BYTE("sput-byte"),
    SPUT_CHAR("sput-char"),
    SPUT_OBJECT("sput-object"),
    SPUT_SHORT("sput-short"),
    SPUT_WIDE("sput-wide"),
    SUB_DOUBLE("sub-double"),
    SUB_DOUBLE_2ADDR("sub-double/2addr"),
    SUB_FLOAT("sub-float"),
    SUB_FLOAT_2ADDR("sub-float/2addr"),
    SUB_INT("sub-int"),
    SUB_INT_2ADDR("sub-int/2addr"),
    SUB_LONG("sub-long"),
    SUB_LONG_2ADDR("sub-long/2addr"),
    THROW("throw"),
    USHR_INT("ushr-int"),
    USHR_INT_2ADDR("ushr-int/2addr"),
    USHR_INT_LIT8("usht-int/lit8"),
    USHR_LONG("ushr-long"),
    USHR_LONG_2ADDR("ushr-long/2addr"),
    XOR_INT("xor-int"),
    XOR_INT_2ADDR("xor-int/2addr"),
    XOR_INT_LIT16("xor-int/lit16"),
    XOR_INT_LIT8("xor-int/lit8"),
    XOR_LONG("xor-long"),
    XOR_LONG_2ADDR("xor-long/2addr"),
    NO_INST("no-inst")
}

/**
 * Returns a Map<String, Instruction> containing all the operation codes as keys and their corresponding Instruction
 * as values.
 */
fun getInstructionMap() :Map<String, Instruction> {
    val instMap: MutableMap<String, Instruction> = mutableMapOf()
    for (inst in Instruction.values()){
        instMap[inst.opcode] = inst
    }

    return instMap.toMap()
}

/**
 * Returns all possible permutations of the n-grams as a nested List (list containing n-grams). Supports a maximum
 * n-gram size of 4.
 */
fun getInstructionNgrams(ngramSize: Int) : List<List<Instruction>> {
    var ngramList = mutableListOf<List<Instruction>>()
    val instructions = Instruction.values()
    for(i in 0 until instructions.size){
        if(ngramSize>1){
            if(ngramSize>2){
                for(j in 0 until instructions.size){
                    if(ngramSize>3){
                        for(k in 0 until instructions.size){
                            for(l in 0 until instructions.size){
                                ngramList.add(listOf(instructions[i], instructions[j],instructions[k],instructions[l]))
                            }
                        }
                    } else {
                        for(k in 0 until instructions.size){
                            ngramList.add(listOf(instructions[i],instructions[j],instructions[k]))
                        }
                    }
                }
            } else {
                for(j in 0 until instructions.size){
                    ngramList.add(listOf(instructions[i], instructions[j]))
                }
            }
        } else {
            ngramList.add(listOf(instructions[i]))
        }
    }

    ngramList.removeAll { list -> list.contains(Instruction.NO_INST) }
    return ngramList.toList()
}