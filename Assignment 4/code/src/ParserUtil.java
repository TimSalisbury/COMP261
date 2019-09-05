import java.util.*;
import java.util.regex.Pattern;

class ParserUtil {

    /**
     * Checks if the next token in the scanner is equal to the pattern provided, if it is then the function skips over it. If the pattern
     * is not found an exception is thrown.
     * @param s                 The scanner
     * @param node              The node name
     * @param expectedPattern   The expected token
     * @throws ParserFailureException   If the token is not found then this is thrown.
     */
    private static void requireToken(Scanner s, String node, Pattern expectedPattern) throws ParserFailureException {
        expectToken(s, node, expectedPattern);
        s.next();
    }

    /**
     * Checks if the next token in the scanner is equal to the pattern provided. If the pattern is not found an exception is thrown.
     * @param s                 The scanner
     * @param node              The node name
     * @param expectedPattern   The expected token
     * @throws ParserFailureException   If the token is not found then this is thrown.
     */
    private static void expectToken(Scanner s, String node, Pattern expectedPattern) throws ParserFailureException {
        if (!s.hasNext(expectedPattern)) {
            throw new ParserFailureException("Incorrect " + node + " Node: found \"" + s.next() + "\" instead of \"" + expectedPattern.toString() + "\"");
        }
    }

    /**
     * Parses a program node from the provided scanner, PROG ::= STMT*
     * @param s The scanner to parse from
     * @return  The program node parsed
     */
    static ProgramNode parseProgram(Scanner s) {
        List<RobotProgramNode> statements = new ArrayList<>();

        DeclarationNode node = new DeclarationNode(new ArrayList<>());

        if(s.hasNext(DeclarationNode.DECLARATION_PATTERN)){
            parseDeclaration(s, node);

        }

        node.setRoot(true);

        while (s.hasNext()) {
            statements.add(parseStatement(s));
        }

        return new ProgramNode(statements, node);
    }

    /**
     * Adds to a declaration node from the provided scanner, "vars" VAR [ "," VAR ]* ";"
     * @param s     The scanner to parse from
     * @param node  The declaration node to add to
     * @return      The final declaration node
     */
    static DeclarationNode parseDeclaration(Scanner s, DeclarationNode node){
        requireToken(s, "Declaration", DeclarationNode.DECLARATION_PATTERN);
        while(s.hasNext(VariableNode.VARIABLE_PATTERN)){
            node.addVariable(parseVariable(s));
            if(s.hasNext(Parser.COMMA)) s.next();
        }
        requireToken(s, "Declaration", Parser.SEMICOLON);

        return node;
    }

    /**
     * Parses a statement node from the provided scanner, STMT ::= ACT ";" | LOOP | IF | WHILE | ASSGN ";"
     * @param s The scanner to parse from
     * @return  The statement node parsed
     */
    private static StatementNode parseStatement(Scanner s) {

        if (s.hasNext(ActNode.ACT_PATTERN)) {       //If the statement is a act node
            RobotProgramNode node = parseAct(s);
            ParserUtil.requireToken(s, "Statement", Parser.SEMICOLON);  //We require a semi-colon after the act arguments
            return new StatementNode(node);
        } else if (s.hasNext(LoopNode.LOOP_PATTERN)) {  //If the statement is a loop node
            return new StatementNode(parseLoop(s));
        } else if (s.hasNext(IfNode.IF_PATTERN)) {      //If the statement is an if node
            return new StatementNode(parseIf(s));
        } else if (s.hasNext(WhileNode.WHILE_PATTERN)) {   //If the statement is a while node
            return new StatementNode(parseWhile(s));
        } else if (s.hasNext(VariableNode.VARIABLE_PATTERN)) {  //If the statement is a assignment node
            StatementNode node = new StatementNode(parseAssign(s));
            requireToken(s, "Statement", Parser.SEMICOLON); //We require a semi-colon after assignment arguments
            return node;
        }

        //If none of those are found then we throw an error
        throw new ParserFailureException("Incorrect Statement Node: found " + s.next() + " instead of statement non-terminal");
    }

    /**
     * Parses an act node from the provided scanner, ACT ::= "move" [ "(" EXP ")" ] | "turnL" | "turnR" | "turnAround" |
     * @param s The scanner to parse from
     * @return  The act node parsed
     */
    private static ActNode parseAct(Scanner s) {
        switch (s.next()) {
            case "move":
                if (s.hasNext(Parser.OPENPAREN)) {  //If this move function has the optional arguments of how many steps to move
                    s.next();

                    ExpressionNode node = parseExpression(s);

                    requireToken(s, "Act", Parser.CLOSEPAREN);  //Require a closed parenthesis

                    return new ActNode(ActNode.Action.MOVE, node);
                }
                return new ActNode(ActNode.Action.MOVE);
            case "turnL":
                return new ActNode(ActNode.Action.TURNLEFT);
            case "turnR":
                return new ActNode(ActNode.Action.TURNRIGHT);
            case "takeFuel":
                return new ActNode(ActNode.Action.TAKEFUEL);
            case "wait":
                if (s.hasNext(Parser.OPENPAREN)) {  //If this wait function has the optional argument of how many steps to wait
                    s.next();

                    ExpressionNode node = parseExpression(s);

                    requireToken(s, "Act", Parser.CLOSEPAREN);  //Require a closed parenthesis
                    return new ActNode(ActNode.Action.WAIT, node);
                }
                return new ActNode(ActNode.Action.WAIT);
            case "turnAround":
                return new ActNode(ActNode.Action.TURNAROUND);
            case "shieldOn":
                return new ActNode(ActNode.Action.SHIELDON);
            case "shieldOff":
                return new ActNode(ActNode.Action.SHIELDOFF);
        }

        //If none of the possible act nodes are found we throw an error
        throw new ParserFailureException("Incorrect Act Node: found " + s.next() + " when expecting Act non-terminal");
    }

    /**
     * Parses a loop node from the provided scanner, LOOP ::= "loop" BLOCK
     * @param s The scanner to parse from
     * @return  The loop node parsed
     */
    private static LoopNode parseLoop(Scanner s) {
        ParserUtil.requireToken(s, "Loop", LoopNode.LOOP_PATTERN);  //We require the key-word (non-terminal) "loop"
        return new LoopNode(parseBlock(s)); //Create loop node with the following block node
    }

    /**
     * Parses an if node from the provided scanner, IF ::= "if" "(" COND ")" BLOCK [ "elif"  "(" COND ")"  BLOCK ]* [ "else" BLOCK ]
     * @param s The scanner to parse from
     * @return  The if node parsed
     */
    private static IfNode parseIf(Scanner s) {

        ParserUtil.requireToken(s, "If", IfNode.IF_PATTERN);    //We require the non-terminal "if"

        ParserUtil.requireToken(s, "If", Parser.OPENPAREN);     //We require a open parenthesis

        ConditionNode condition = parseCondition(s);    //Parse the condition of this if statement

        ParserUtil.requireToken(s, "If", Parser.CLOSEPAREN);    //Require a closed parenthesis

        BlockNode ifBlock = parseBlock(s);  //Parse the block (body) of the if statement

        List<IfNode.ElseIfNode> elseIfs = null;

        while(s.hasNext(IfNode.ElseIfNode.ELSEIF_PATTERN)){ //If and while there are elif's to parse we need to parse them
            if(elseIfs == null) elseIfs = new ArrayList<>();    //If this is the first elif, then ensure the list is not null

            elseIfs.add(parseElseIfNode(s));
        }

        BlockNode elseBlock = null;
        if (s.hasNext(IfNode.ELSE_PATTERN)) { //If there is an else statement we need to get the block for it
            s.next();
            elseBlock = parseBlock(s);
        }

        return new IfNode(ifBlock, elseIfs, elseBlock, condition);
    }

    /**
     * Parses an elif node from the scanner, ELIF ::= "elif" "(" COND ")" BLOCK
     * @param s The scanner to parse from
     * @return  The parsed elif node
     */
    private static IfNode.ElseIfNode parseElseIfNode(Scanner s){
        s.next();   //Skip the "elif" statement, we already checked it was there in the IfNode parser

        requireToken(s, "Else If", Parser.OPENPAREN);   //Require an open parenthesis for the condition

        ConditionNode condition = parseCondition(s);

        requireToken(s, "Else If", Parser.CLOSEPAREN);  //Require a closed parenthesis for the condition

        BlockNode block = parseBlock(s);

        return new IfNode.ElseIfNode(condition, block);
    }

    /**
     * Parses a while node fro the scanner, WHILE ::= "while" "(" COND ")" BLOCK
     * @param s The scanner to parse from
     * @return  The parsed while loop
     */
    private static WhileNode parseWhile(Scanner s) {
        ParserUtil.requireToken(s, "While", WhileNode.WHILE_PATTERN);   //Require the non-terminal "while"

        ParserUtil.requireToken(s, "While", Parser.OPENPAREN);  //Require an open parenthesis for the condition

        ConditionNode condition = parseCondition(s);

        ParserUtil.requireToken(s, "While", Parser.CLOSEPAREN); //Require a closed parenthesis for the condition

        BlockNode block = parseBlock(s);

        return new WhileNode(condition, block);
    }

    /**
     * Parses an assignment node from the provided scanner, ASSGN ::= VAR "=" EXP
     * @param s The scanner to parse from
     * @return  The assignment node parsed
     */
    private static AssignNode parseAssign(Scanner s) {
        VariableNode variable = parseVariable(s);       //Parse the variable we are assigning

        ParserUtil.requireToken(s, "Assign", Parser.EQUALS);    //Require a "=" for assignment

        ExpressionNode expression = parseExpression(s); //Parse the expression we are assigning variable to

        return new AssignNode(variable, expression);
    }

    /**
     * Parses a block node from the provided scanner, BLOCK ::= "{" STMT+ "}"
     * @param s The scanner to parse from
     * @return  The block node pasrsed
     */
    private static BlockNode parseBlock(Scanner s) {
        ParserUtil.requireToken(s, "Block", Parser.OPENBRACE);   //We require a open brace for the block of code

        DeclarationNode declarationNode = new DeclarationNode(new ArrayList<>());
        if(s.hasNext(DeclarationNode.DECLARATION_PATTERN)){
            parseDeclaration(s, declarationNode);
        }

        List<StatementNode> statements = new ArrayList<>();  //Create a list of statements inside of block

        while (!s.hasNext(Parser.CLOSEBRACE)) { //While there are still statements to parse we add them to the list
            statements.add(parseStatement(s));
        }

        ParserUtil.requireToken(s, "Block", Parser.CLOSEBRACE); //And finally we require a closed brace for the block of code

        return new BlockNode(statements, declarationNode);
    }

    /**
     * Parses an expression node from the scanner, EXP ::= NUM | SEN | VAR | OP
     * @param s The scanner to parse from
     * @return  The expression node parsed
     */
    private static ExpressionNode parseExpression(Scanner s) {
        if (s.hasNext(NumberNode.NUMBER_PATTERN)) {
            return new ExpressionNode(parseNumber(s));
        } else if (s.hasNext(SensorNode.SENSOR_PATTERN)) {
            return new ExpressionNode(parseSensor(s));
        } else if (s.hasNext(VariableNode.VARIABLE_PATTERN)) {
            return new ExpressionNode(parseVariable(s));
        } else if (s.hasNext(OperatorNode.OPERATOR_PATTERN)) {
            return new ExpressionNode(parseOperator(s));
        }

        //If none of the possible options for an expression node are found we throw an error
        throw new ParserFailureException("Incorrect Expression Node: found \"" + s.next() + "\" instead of Expression non-terminal");
    }

    /**
     * Parses a condition node from the scanner, COND ::= RELOP "(" EXP "," EXP ")"  | and ( COND, COND ) | or ( COND, COND )  | not ( COND )
     * @param s The scanner to parse from
     * @return  The condition node parsed
     */
    private static ConditionNode parseCondition(Scanner s) {
        ParserUtil.expectToken(s, "Condition", ConditionNode.CONDITION_PATTERN);    //We require a condition non-terminal

        ConditionNode.Comparison comparison = null;
        switch (s.next()) {
            case "lt":
                comparison = ConditionNode.Comparison.LESS_THAN;
                break;
            case "gt":
                comparison = ConditionNode.Comparison.GREATER_THAN;
                break;
            case "eq":
                comparison = ConditionNode.Comparison.EQUAL_TO;
                break;
            case "and":
                comparison = ConditionNode.Comparison.AND;
                break;
            case "or":
                comparison = ConditionNode.Comparison.OR;
                break;
            case "not":
                comparison = ConditionNode.Comparison.NOT;
                break;
        }
        ParserUtil.requireToken(s, "Condition", Parser.OPENPAREN);
        if (comparison == ConditionNode.Comparison.LESS_THAN || comparison == ConditionNode.Comparison.GREATER_THAN ||
                comparison == ConditionNode.Comparison.EQUAL_TO) {  //If a comparison of values


            RobotValueNode sensor = parseExpression(s);

            ParserUtil.requireToken(s, "Condition", Parser.COMMA);

            RobotValueNode number = parseExpression(s);

            ParserUtil.requireToken(s, "Condition", Parser.CLOSEPAREN);

            return new ConditionNode(comparison, sensor, number);
        } else {    //If a logical operation
            RobotConditionNode conditionOne = parseCondition(s);

            if (comparison == ConditionNode.Comparison.NOT) {   //If the logical operation is a "not"
                ParserUtil.requireToken(s, "Condition", Parser.CLOSEPAREN);
                return new ConditionNode(comparison, conditionOne, null);
            }

            ParserUtil.requireToken(s, "Condition", Parser.COMMA);

            RobotConditionNode conditionTwo = parseCondition(s);

            ParserUtil.requireToken(s, "Condition", Parser.CLOSEPAREN);

            return new ConditionNode(comparison, conditionOne, conditionTwo);
        }
    }

    /**
     * Parses a sensor node from the scanner, SEN ::= "fuelLeft" | "oppLR" | "oppFB" | "numBarrels" |
     *           "barrelLR" [ "(" EXP ")" ] | "barrelFB" [ "(" EXP ")" ] | "wallDist"
     * @param s The scanner to parse from
     * @return  The sensor node parsed
     */
    private static SensorNode parseSensor(Scanner s) {
        String token = s.next();    //Skip the non-terminal
        switch (token) {
            case "fuelLeft":
                return new SensorNode(SensorNode.Sensor.FUEL_LEFT);
            case "oppLR":
                return new SensorNode(SensorNode.Sensor.OPPONENT_LEFT_RIGHT);
            case "oppFB":
                return new SensorNode(SensorNode.Sensor.OPPONENT_FORWARD_BACK);
            case "numBarrels":
                return new SensorNode(SensorNode.Sensor.NUMBER_BARRELS);
            case "barrelLR":
                if(s.hasNext(Parser.OPENPAREN)){    //If "barrelLR" has the optional argument
                    s.next();

                    ExpressionNode node = parseExpression(s);

                    requireToken(s, "Sensor", Parser.CLOSEPAREN);

                    return new SensorNode(node, SensorNode.Sensor.BARREL_LEFT_RIGHT);
                }
                return new SensorNode(SensorNode.Sensor.BARREL_LEFT_RIGHT);
            case "barrelFB":
                if(s.hasNext(Parser.OPENPAREN)){    //If "barrelFB" has the optional argument
                    s.next();

                    ExpressionNode node = parseExpression(s);

                    requireToken(s, "Sensor", Parser.CLOSEPAREN);

                    return new SensorNode(node, SensorNode.Sensor.BARREL_FORWARD_BACK);
                }
                return new SensorNode(SensorNode.Sensor.BARREL_FORWARD_BACK);
            case "wallDist":
                return new SensorNode(SensorNode.Sensor.WALL_DISTANCE);
        }

        //If none of the possible options for a sensor node are found we throw an error
        throw new ParserFailureException("Incorrect Sensor Node: found \"" + token + "\" instead of \"" + SensorNode.SENSOR_PATTERN.toString() + "\"");
    }

    /**
     * Parses an operator node from the scanner, OP ::= "add" | "sub" | "mul" | "div"
     * @param s The scanner to parse from
     * @return  The operator node parsed
     */
    private static OperatorNode parseOperator(Scanner s) {
        expectToken(s, "Operator", OperatorNode.OPERATOR_PATTERN); //Expect an operator non-terminal

        OperatorNode.Operator operator = null;
        switch (s.next()) {
            case "add":
                operator = OperatorNode.Operator.ADD;
                break;
            case "sub":
                operator = OperatorNode.Operator.SUB;
                break;
            case "mul":
                operator = OperatorNode.Operator.MUL;
                break;
            case "div":
                operator = OperatorNode.Operator.DIV;
                break;
        }

        requireToken(s, "Operator", Parser.OPENPAREN);

        ExpressionNode expressionOne = parseExpression(s);

        requireToken(s, "Operator", Parser.COMMA);

        ExpressionNode expressionTwo = parseExpression(s);

        requireToken(s, "Operator", Parser.CLOSEPAREN);

        return new OperatorNode(operator, expressionOne, expressionTwo);
    }

    /**
     * Parses a variable node from the scanner, VAR ::= "\\$[A-Za-z][A-Za-z0-9]*"
     * @param s The scanner to parse from
     * @return  The variable node parsed
     */
    private static VariableNode parseVariable(Scanner s) {
        expectToken(s, "Variable", VariableNode.VARIABLE_PATTERN);  //Require a variable name

        return new VariableNode(s.next());
    }

    /**
     * Parses a number node from the scanner, NUM ::= "-?[0-9]+"
     * @param s The scanner to parse from
     * @return  The number node parsed
     */
    private static NumberNode parseNumber(Scanner s) {
        expectToken(s, "Number", NumberNode.NUMBER_PATTERN);
        return new NumberNode(s.nextInt());
    }

}
