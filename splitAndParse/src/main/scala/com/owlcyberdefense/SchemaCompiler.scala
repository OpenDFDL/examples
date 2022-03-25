package com.owlcyberdefense

import org.apache.daffodil.sapi.ValidationMode
import org.apache.daffodil.sapi.Diagnostic
import org.apache.daffodil.sapi.Daffodil

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.net.URL
import java.nio.channels.Channels

object SchemaCompiler {

  def initDP(schemaFileURL: URL, rootName: String, rootNS: String): (Processor, Seq[Diagnostic]) = {
    //
    // First compile the DFDL Schema
    val c = Daffodil.compiler
    val pf = c.compileSource(schemaFileURL.toURI).withDistinguishedRootNode(rootName, rootNS)
    val pfDiags = pf.getDiagnostics
    if (pf.isError) {
      throw new SchemaCompiler.CompileFailure(pfDiags)
    }
    var dp = pf.onPath("/")
    val dpDiags = dp.getDiagnostics
    if (dp.isError) throw new SchemaCompiler.CompileFailure(dpDiags)
    val compilationWarnings = if (!pfDiags.isEmpty) pfDiags else dpDiags // dpDiags might be empty. That's ok.

    //
    // Save and reload the schema.
    //
    // This isn't necessary but is a step towards caching the schema so that
    // one need not wait for schema compilation each time this utility is used.
    // (Large DFDL schemas can take many seconds to compile, and that startup time
    // may be undesirable.)
    //
    // An enhancement might be to allow pre-compiled DFDL schemas to be passed to this
    // utility instead of schema source URLs.
    //
    val savedDP = {
      val bbos = new ByteArrayOutputStream()
      val wbc = Channels.newChannel(bbos)
      dp.save(wbc)
      bbos.toByteArray
    }

    dp = Daffodil.compiler().reload(Channels.newChannel(new ByteArrayInputStream(savedDP)))
    val proc = new Processor(dp.withValidationMode(ValidationMode.Limited))
    (proc, compilationWarnings)
  }

  case class CompileFailure(diags: Seq[Diagnostic])
    extends Exception("DFDL Schema Compile Failure") {
    override def toString() = getMessage() + "\n" + diags.mkString("\n")
  }
}