;;;; foil.asd

(asdf:defsystem #:foil
  :depends-on (#:trivial-garbage)
  :serial t
  :components
  ((:module "lisp"
    :components((:module "src"
                 :components((:file "foil")))))))

