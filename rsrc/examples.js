function examples(options){
     var settings = $.extend({
        phpId: 'php',
        exampleId: 'example'
    }, options );   

    var examples = [
        "",
        //----------------------------------------------------------------------------
        //simple example
        //----------------------------------------------------------------------------
        '<?php\n' +
        'function addition($x, $y){\n' +
        '  return $x + $y;\t\n' +
        '}\n' +
        '\n' +
        '$a = addition(1, 2);\n' +
        '$a = addition(1.5, 2);\n' +
        '$b = addition([1, 2], [2]); //second parameter should be an array\n' +
        '$c = addition($a, \'2 bananas\');\n' +
        '?>',
        //----------------------------------------------------------------------------
        // kinds of polymorphism
        //----------------------------------------------------------------------------
        '<?php\n' +
        '\n' +
        '//data polymorphism\n' +
        '$x = 1;\n' +
        '$x = 1.5;\n' +
        '\n' +
        '//parametric polymorphism\n' +
        'function identity($x){\n' +
        '\treturn $x;\n' +
        '}\n' +
        '\n' +
        '//bounded polymorphism\n' +
        'function plusOne($x){\n' +
        '\treturn $x + 1;\n' +
        '}\n' +
        '$a = identity(1);\n' +
        '$b = plusOne(2);\n' +
        '$c = plusOne(2.5);\n' +
        '\n' +
        '//adhoc polymorphism\n' +
        'function addition($x, $y){\n' +
        '\treturn $x + $y;\n' +
        '}\n' +
        '$d = addition(1, 3);\n' +
        '$e = addition(1, 2.5);\n' +
        '$f = addition([1,2],[2.3, 1]);\n' +
        '\n' +
        '//intensional polymorphism\n' +
        'function plusOneOrError($x) {\n' +
        '  if (\\is_int($x)) {\n' +
        '    return $x + 1;\n' +
        '  }\n' +
        '  return \'not an int given:\' . $x;\n' +
        '}\n' +
        '\n' +
        '$g = plusOneOrError(1);\n' +
        '$h = plusOneOrError(1.2);\n' +
        '$i = plusOneOrError(\'a\');\n' +
        '$j = plusOneOrError([1, \'a\']);\n' +
        '?>\n'
        ,
        //----------------------------------------------------------------------------
        //recursion
        //----------------------------------------------------------------------------
          '<?php\n' +
          '\n' +
          '//direct recursion\n' +
          'function fac($n){\n' +
          '  return $n > 0 ? $n * fac($n-1) : 1;\n' +
          '}\n' +
          '\n' +
          '$g = fac(5);\n' +
          '$h = fac(1.5);\n' +
          '\n' +
          '//indirect recursion\n' +
          'function foo($x){ \n' +
          '  return $x > 0 ? bar($x-1) : $x;\n' +
          '}\n' +
          'function bar($x){ \n' +
          '  return $x > 0 ? foo($x-1) : $x;\n' +
          '}\n' +
          '\n' +
          '$i = bar(1);\n' +
          '$j = foo(1.5);\n' +
          '\n' +
          '//ad-hoc polymorphic indirect recursion\n' +
          'function rec1($x, $y){ \n' +
          '  return $x > 0 ? rec2($x + $y, $y) : $x; \n' +
          '}\n' +
          'function rec2($x, $y){ \n' +
          '  return $x > 10 ? rec1($x + $y, $y) : $y;\n' +
          '}\n' +
          '\n' +
          '$k = rec1(5, 8);\n' +
          '$l = rec1(1.3, 4.6);\n' +
          '$m = rec2([1, 2], [2]);\n' +
          '\n' +
          '?>\n'
        ,
        //----------------------------------------------------------------------------
        //Runtime checks
        //----------------------------------------------------------------------------
        '<?php\n' +
        '\n' +
        'function getOrDefault($arr, $key, $defaultValue) {\n' +
        '\tif (array_key_exists($key, $arr)) {\n' +
        '\t\treturn (string) $arr[$key];\n' +
        '\t}\n' +
        '\treturn $defaultValue;\n' +
        '}\n' +
        '\n' +
        '$a = getOrDefault([1], 0, 1);\n' +
        '$b = getOrDefault([\'a\' => 1], \'a\', \'not defined\');\n' +
        '\n' +
        'function foo($x) {\n' +
        '\tif (is_array($x)) {\n' +
        '\t\treturn $x + [1];\n' +
        '\t}\n' +
        '\treturn ~($x / 2);\n' +
        '}\n' +
        '\n' +
        '$c = foo(1);\n' +
        '$d = foo(1.2);\n' +
        '$e = foo([1]);\n' +
        '\n' +
        '?>',
        //----------------------------------------------------------------------------
        //Lot of Parameters
        //----------------------------------------------------------------------------
        '<?php\n' +
        'function add(\n' +
        '\t$a1,  $a2,  $a3,  $a4,  $a5,\n' +
        '\t$a6,  $a7,  $a8,  $a9,  $a10,\n' +
        '\t$a11, $a12, $a13, $a14, $a15,\n' +
        '\t$a16, $a17, $a18, $a19, $a20) {\n' +
        '\t\n' +
        '\treturn $a1  + $a2  + $a3  + $a4  + $a5\n' +
        '\t\t+ $a6  + $a7  + $a8  + $a9  + $a10\n' +
        '\t\t+ $a11 + $a12 + $a13 + $a14 + $a15\n' +
        '\t\t+ $a16 + $a17 + $a18 + $a19 + $a20;\n' +
        '}\n' +
        '?>',
        //----------------------------------------------------------------------------
        //Double definitions
        //----------------------------------------------------------------------------
        '<?php\n' +
        '// double definitions result in "fatal errors" \n' +
        '// because type inference could no longer be executed\n' +
        '// without potential errors.\n' +
        '// You can find further information here:\n' +
        '// http://tsphp.ch/wiki/display/TINS/System+Specification+of+TinsPHP#SystemSpecificationofTinsPHP-Doubledefinitions\n' +
        '\n' +
        'const a = 1;\n' +
        'const a = "hi";\t\n' +
        '\n' +
        'function foo(){return;}\n' +
        'function Foo(){return;} //case insensitive\n' +
        '\n' +
        'function bar($a, $a){return;}\n' +
        '?>\n',
        //----------------------------------------------------------------------------
        //Unitialised variables
        //----------------------------------------------------------------------------
          '<?php\n' +
          '// Uninitialised variables are problematic, because PHP\n' +
          '// uses `null` in such cases. A similar problem arises\n' +
          '// in case of a forward reference usage of a constant. \n' +
          '// In such a case a string is used with the name of\n' +
          '// the constant as value. Both problems dilute the infered \n' +
          '// result and I would not want it.\n' +
          '// Theoretically, the first problem could be takeld without\n' +
          '// much effort. In the following example, it would infer the \n' +
          '// union type {int, null} for $a and not int as expected.\n' +
          '// The second problem is more difficult because it would involve\n' +
          '// a transformation of the constant into a variable.\n' +
          '// You can find further information here:\n' +
          '// https://tsphp.ch/wiki/display/TINS/System+Specification+of+TinsPHP#SystemSpecificationofTinsPHP-Forwardreferenceusage.1\n' +
          '\n' +
          'echo $a;\n' +
          '$a = 1;\n' +
          '\n' +
          '$b = a; \n' +
          'const a = 1;\n' +
          '?>'
        ,
    ];
    var functions = {
        show:
            function(nr){
                var example = examples[nr];
                if(example != undefined){
                    $('#' + settings.phpId).val(example);
                }
            }
    };
    
    $('#' + settings.exampleId).change(function(){
        functions.show($('#' + settings.exampleId).val());
    });
    
    return functions;
}