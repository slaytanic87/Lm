# Lm

This is a Kotlin based demo compiler for a functional language called Lm
(Under contruction!)
## Grammar

_prog_      -> program _scope_\
_scope_     -> { _sdecls_ } | € \
_sdecls_    -> _decl_ _sdecls_ | _function_ _sdecls_ | € \
_function_  -> _type_ func id _param_ _block_ \
_param_     -> ( _pdecls_ ) | () \
_pdecls_    -> _type_ id , _pdecls_ | _type_ id \
_block_   -> { _decls_ _stmts_ } \
_decls_   -> _decl_ _decls_ | € \
_decl_    -> _type_ _ids_ ; \
_ids_     -> id , _ids_ | id \
_type_    -> basic _dims_ \
_dims_    -> [ num ] _dims_ | € \
_stmts_   -> _stmt_ _stmts_ | € \
_stmt_    -> ; \
         | if ( _bool_ ) _stmt_ \
         | if ( _bool_ ) _stmt_ else _stmt_ \
         | while ( _bool_ ) _stmt_ \
         | do _stmt_ while ( _bool_ ) ; \
         | for ( _assign_ ; _bool_ ; _assign_ ) _stmt_ \
         | break ; \
         | _block_ \
         | _assign_ ; \
         | return _bool_ ; \
         | _bool_ ; \
_funcall_   -> id (paramcall) \
_paramcall_ -> bool, paramcall | bool \
_assign_   -> id _offset_ = _bool_ \
_offset_   -> [ id ] _offset_ | [ num ] _offset_ | € \
_bool_     -> _bool_ or _join_ | _join_ \
_join_     -> _join_ and _equality_ | _equality_ \
_equality_ -> _equality_ eq _rel_ | _equality_ ne _rel_ | _rel_ \
_rel_      -> _expr_ < _expr_ | _expr_ le _expr_ | _expr_ ge _expr_ | _expr_ > _expr_ | _expr_ \
_expr_     -> _expr_ + _term_ | _expr_ - _term_ | _term_ \
_term_     -> _term_ * _unary_ | _term_ / _unary_ | _unary_ \
_unary_    -> ! _unary_ | - _unary_ | _factor_ \
_factor_   -> ( _bool_ ) | _funcall_ | id _offset_ | num | real | bool | true | false


| Token classes                       |           |
|-------------------------------------|:---------:|
|id   : identifier                    | or  = \|  |
|num  : Integer-number                | and = &&  |
|real : Floating Point number         | eq  = ==  |
|char : sign                          | ne  = !=  |
|bool : boolean value                 | le  = \<= |
|basic = num \| real \| char \| bool  | ge  = >=  |

### reserves words
if, else, while, do, for, break, true, false, program, func, return  

## Example
```
program {
  int i; int j; float v;
  float x; float[100] a;
  int func demo (int p) {
    while( true ) {
        do i = i + 1; while( a[i] < v);
        do j = j-1; while( a[j] > v);
        if ( i >= j ) break;
        x = a[i]; a[i] = a[j]; a[j] = x;
    }
    return 2;
  }
}
```

```
program {
  float v; float[100] a;
  float func myfunction (float f){
    while (true) {
        int i; int j;
        do i = i+1; while (a[i] < v);
        do j = j-1; while (a[j] > v);
        if (i >= j) break;
        {
            float x;
            x = a[i]; a[i] = a[j]; a[j] = x;
        }
        return j;
    }
  }
}
```
